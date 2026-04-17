#!/usr/bin/env bash
# Usage:
#   scripts/release.sh 0.1.2 "Headline title" "Release notes body"
#
# Steps:
#   1. Bumps versionCode (by +1 from current) and versionName in app/build.gradle.kts
#   2. Commits bump + pushes to main
#   3. Builds debug APK (with TEMP override for Cyrillic user path)
#   4. Creates GitHub release via API
#   5. Uploads APK to that release
#
# Prereq: $HOME/../../tmp/.ghtok (or /c/dev/tmp/.ghtok) contains a valid PAT with `repo` scope.

set -euo pipefail

VERSION="${1:?Usage: release.sh <version> <title> <body>}"
TITLE="${2:?Usage: release.sh <version> <title> <body>}"
BODY="${3:?Usage: release.sh <version> <title> <body>}"

REPO="blizzardsnake/herotraining1"
PROJECT="/c/dev/hero-training-android"
TOKEN_FILE="/c/dev/tmp/.ghtok"
TMP="/c/dev/tmp/release-work"

if [[ ! -f "$TOKEN_FILE" ]]; then
  echo "ERROR: no token at $TOKEN_FILE — paste PAT into this file first (40 chars, no newline)"; exit 1
fi
mkdir -p "$TMP"

cd "$PROJECT"

# --- 1. Bump versionCode + versionName ---
CUR_CODE=$(grep -Eo 'versionCode = [0-9]+' app/build.gradle.kts | grep -Eo '[0-9]+')
NEW_CODE=$((CUR_CODE + 1))
sed -i.bak "s/versionCode = $CUR_CODE/versionCode = $NEW_CODE/" app/build.gradle.kts
sed -i.bak "s/versionName = \"[^\"]*\"/versionName = \"$VERSION\"/" app/build.gradle.kts
rm -f app/build.gradle.kts.bak
echo "bumped: versionCode $CUR_CODE -> $NEW_CODE, versionName -> $VERSION"

# --- 2. Commit + push ---
git add app/build.gradle.kts
git -c user.email="dev@herotraining.local" -c user.name="Hero Training Dev" \
  commit -m "chore(release): bump to v$VERSION (code $NEW_CODE)"
git push origin main

# --- 3. Build debug APK ---
powershell.exe -NoProfile -Command "
\$env:JAVA_HOME='C:\dev\jdk17'
\$env:ANDROID_HOME='C:\dev\android-sdk'
\$env:TEMP='C:\dev\tmp'; \$env:TMP='C:\dev\tmp'
\$env:GRADLE_USER_HOME='C:\dev\gradle-home'
\$env:Path='C:\dev\jdk17\bin;' + \$env:Path
Set-Location 'C:\dev\hero-training-android'
& '.\gradlew.bat' assembleDebug
" | tail -5

APK="$PROJECT/app/build/outputs/apk/debug/app-debug.apk"
if [[ ! -f "$APK" ]]; then echo "ERROR: APK not found at $APK"; exit 1; fi
echo "built: $APK ($(stat -c %s "$APK") bytes)"

# --- 4. Create release via API ---
ESC_BODY=$(printf '%s' "$BODY" | python -c 'import json,sys; print(json.dumps(sys.stdin.read()))' 2>/dev/null || printf '"%s"' "$BODY")
cat > "$TMP/body.json" <<EOF
{
  "tag_name": "v$VERSION",
  "target_commitish": "main",
  "name": "v$VERSION — $TITLE",
  "body": $ESC_BODY,
  "draft": false,
  "prerelease": false,
  "make_latest": "true"
}
EOF

TOKEN=$(cat "$TOKEN_FILE")

curl -sS -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/vnd.github+json" \
  "https://api.github.com/repos/$REPO/releases" \
  -d @"$TMP/body.json" \
  -o "$TMP/release.json"

RELEASE_ID=$(grep -oE '"id": [0-9]+' "$TMP/release.json" | head -1 | grep -oE '[0-9]+')
if [[ -z "$RELEASE_ID" ]]; then
  echo "ERROR: failed to create release"; cat "$TMP/release.json"; exit 1
fi
echo "release created: id=$RELEASE_ID"

# --- 5. Upload APK ---
ASSET_NAME="hero-training-$VERSION.apk"
curl -sS -X POST \
  -H "Authorization: Bearer $TOKEN" \
  -H "Accept: application/vnd.github+json" \
  -H "Content-Type: application/vnd.android.package-archive" \
  --data-binary @"$APK" \
  "https://uploads.github.com/repos/$REPO/releases/$RELEASE_ID/assets?name=$ASSET_NAME" \
  -o "$TMP/upload.json"

STATE=$(grep -oE '"state":"[^"]+"' "$TMP/upload.json" | head -1)
URL=$(grep -oE '"browser_download_url":"[^"]+"' "$TMP/upload.json" | head -1 | cut -d'"' -f4)

if [[ "$STATE" != *uploaded* ]]; then
  echo "ERROR: upload failed"; cat "$TMP/upload.json"; exit 1
fi
echo ""
echo "✅ Release v$VERSION published"
echo "   $URL"
echo "   https://github.com/$REPO/releases/tag/v$VERSION"

# Cleanup
rm -f "$TMP/body.json" "$TMP/release.json" "$TMP/upload.json"
