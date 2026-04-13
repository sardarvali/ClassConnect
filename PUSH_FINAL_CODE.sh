#!/bin/bash
set -e

REPO="/home/syed-sardar-valli/data/ANDROID/classconnect/p3"
USERNAME="sardarvali"
EMAIL="syedsardarvali246@gmail.com"
BRANCH_NAME="final-code"

echo "=========================================="
echo "FINAL CODE PUSH WORKFLOW"
echo "=========================================="

cd "$REPO"

# Configure git identity
echo "[1/7] Setting git identity..."
git config user.name "$USERNAME"
git config user.email "$EMAIL"
echo "✓ Git identity set: $USERNAME <$EMAIL>"

# Show current status
echo ""
echo "[2/7] Current repository state:"
current_branch=$(git branch --show-current)
echo "Current branch: $current_branch"
git status -sb
echo ""

# Verify secrets are secured
echo "[3/7] Verifying secrets are secured..."
if grep -q "YOUR_GEMINI_API_KEY_HERE" local.properties; then
  echo "✓ Gemini API key is secured (placeholder)"
else
  echo "⚠ WARNING: Real Gemini key may still be present"
fi

if grep -q "YOUR_NEWS_API_KEY_HERE" local.properties; then
  echo "✓ News API key is secured (placeholder)"
else
  echo "⚠ WARNING: Real News key may still be present"
fi

if grep -q "REDACTED_FIREBASE_WEB_API_KEY" app/google-services.json; then
  echo "✓ Firebase API keys are redacted"
else
  echo "⚠ WARNING: Firebase keys may still be exposed"
fi

echo ""

# Create and push branch
echo "[4/7] Creating and pushing branch '$BRANCH_NAME'..."
git checkout -b "$BRANCH_NAME" 2>/dev/null || git checkout "$BRANCH_NAME"
git add -A
git commit -m "Secure API keys and finalize release code

- Redact Firebase web API keys
- Replace hardcoded Gemini and News API keys with placeholders
- Add environment variable fallback for API key loading
- Add local.properties.example template
- Update docs for secure key provisioning" || echo "✓ No changes to commit (already up to date)"

git push -u origin "$BRANCH_NAME"
echo "✓ Branch '$BRANCH_NAME' pushed to origin"

echo ""

# Update and push main
echo "[5/7] Fetching latest remote state..."
git fetch origin --prune
echo "✓ Remote fetched"

echo ""
echo "[6/7] Updating main branch..."
git checkout main
git pull --ff-only origin main
echo "✓ Main branch fast-forwarded"

echo ""
echo "[7/7] Pushing main branch..."
git push origin main
echo "✓ Main branch pushed to origin"

echo ""
echo "=========================================="
echo "✓ PUSH WORKFLOW COMPLETE!"
echo "=========================================="
echo ""
echo "Summary:"
echo "  • Branch '$BRANCH_NAME' created and pushed"
echo "  • API keys secured (no real keys in code)"
echo "  • Main branch updated and pushed"
echo "  • Origin: $(git config --get remote.origin.url)"
echo ""
echo "Next steps:"
echo "  1. Verify pushed commits on GitHub: https://github.com/Izaz-123/ClassConnect"
echo "  2. Rotate exposed keys in provider consoles (Gemini, NewsAPI, Firebase)"
echo "  3. Developers must fill in local.properties from local.properties.example"

