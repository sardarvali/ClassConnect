#!/bin/bash
# ClassConnect Final Code Push - Step by Step

set -e

REPO="/home/syed-sardar-valli/data/ANDROID/classconnect/p3"
cd "$REPO"

echo "=========================================="
echo "CLASSCONNECT FINAL CODE PUSH"
echo "=========================================="
echo ""

# Step 1: Configure Git Identity
echo "[STEP 1] Configuring git identity..."
git config user.name "sardarvali"
git config user.email "syedsardarvali246@gmail.com"
echo "✓ Git configured: sardarvali <syedsardarvali246@gmail.com>"
echo ""

# Step 2: Verify Secrets Are Secured
echo "[STEP 2] Verifying secrets are secured..."
echo "Checking local.properties..."
if grep -q "YOUR_GEMINI_API_KEY_HERE" local.properties && grep -q "YOUR_NEWS_API_KEY_HERE" local.properties; then
    echo "✓ API keys secured with placeholders"
else
    echo "ERROR: API keys still contain real values!"
    exit 1
fi

echo "Checking google-services.json..."
if grep -q "REDACTED_FIREBASE_WEB_API_KEY" app/google-services.json; then
    echo "✓ Firebase keys redacted"
else
    echo "ERROR: Firebase keys not redacted!"
    exit 1
fi
echo ""

# Step 3: Create final-code branch and commit
echo "[STEP 3] Creating 'final-code' branch and committing changes..."
if git rev-parse --verify final-code > /dev/null 2>&1; then
    echo "Branch 'final-code' already exists, checking out..."
    git checkout final-code
else
    echo "Creating new branch 'final-code'..."
    git checkout -b final-code
fi

echo "Adding secured files to staging..."
git add -A

echo "Creating commit..."
git commit -m "Secure API keys and finalize release code

- Redact Firebase web API keys
- Replace hardcoded Gemini and News API keys with placeholders
- Add environment variable fallback for API key loading in build.gradle.kts
- Add local.properties.example template for team setup
- Update SETUP.md documentation for secure provisioning
- No real secrets in versioned code" || echo "✓ No new changes to commit"
echo ""

# Step 4: Push final-code branch
echo "[STEP 4] Pushing 'final-code' branch to GitHub..."
git push -u origin final-code
echo "✓ Branch 'final-code' pushed successfully"
echo ""

# Step 5: Switch to main and update
echo "[STEP 5] Switching to 'main' branch..."
git checkout main
echo ""

# Step 6: Fetch latest and fast-forward
echo "[STEP 6] Fetching latest from remote..."
git fetch origin main
echo ""

echo "[STEP 7] Fast-forwarding main branch..."
git pull --ff-only origin main
echo "✓ Main branch fast-forwarded"
echo ""

# Step 7: Push main
echo "[STEP 8] Pushing 'main' branch to GitHub..."
git push origin main
echo "✓ Main branch pushed successfully"
echo ""

# Final Summary
echo "=========================================="
echo "✅ PUSH WORKFLOW COMPLETED SUCCESSFULLY!"
echo "=========================================="
echo ""
echo "📊 SUMMARY:"
echo "  ✓ Git identity configured"
echo "  ✓ Secrets verified as secured"
echo "  ✓ Branch 'final-code' created and pushed"
echo "  ✓ Main branch updated and pushed"
echo "  ✓ Remote: $(git config --get remote.origin.url)"
echo ""
echo "🔗 VERIFY ON GITHUB:"
echo "  https://github.com/Izaz-123/ClassConnect/branches"
echo ""
echo "⚠️  IMPORTANT SECURITY STEPS:"
echo "  1. Rotate Gemini API key (was exposed in earlier commits)"
echo "  2. Rotate NewsAPI key (was exposed in earlier commits)"
echo "  3. Rotate Firebase web API key (was exposed in earlier commits)"
echo "  4. Share local.properties.example with team for local setup"
echo ""
echo "Current branch: $(git branch --show-current)"
git --no-pager log --oneline -5

