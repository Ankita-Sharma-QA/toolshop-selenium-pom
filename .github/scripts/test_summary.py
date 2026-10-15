"""
Reads TestNG's results file and shows the results on the GitHub Actions run page:
  - a table in the run summary
  - an error annotation for every failed test (with the failure message)
"""
import os
import sys
import xml.etree.ElementTree as ET

RESULTS_FILE = "target/surefire-reports/testng-results.xml"

MAVEN_LOG = "mvn-output.log"

if not os.path.exists(RESULTS_FILE):
    print("::error::No TestNG results found - the tests did not run. Maven errors below.")
    # Show Maven's error lines (e.g. compile errors) as annotations
    if os.path.exists(MAVEN_LOG):
        with open(MAVEN_LOG, encoding="utf-8", errors="ignore") as f:
            errors = [line.strip() for line in f if line.startswith("[ERROR]") and len(line.strip()) > 8]
        for line in errors[:10]:
            print(f"::error::{line[:400]}")
    sys.exit(0)

root = ET.parse(RESULTS_FILE).getroot()

rows = []
for method in root.iter("test-method"):
    if method.get("is-config") == "true":
        continue                      # skip @BeforeMethod / @AfterMethod
    if method.get("retried") == "true":
        continue                      # skip attempts that were retried
    name = method.get("name")
    status = method.get("status")
    seconds = int(method.get("duration-ms", "0")) / 1000
    message = ""
    exception = method.find("exception")
    if exception is not None:
        msg = exception.find("message")
        if msg is not None and msg.text:
            message = msg.text.strip().splitlines()[0][:500]
        else:
            message = exception.get("class", "")
    rows.append((name, status, seconds, message))

passed = sum(1 for r in rows if r[1] == "PASS")
failed = sum(1 for r in rows if r[1] == "FAIL")
skipped = sum(1 for r in rows if r[1] == "SKIP")

icons = {"PASS": "✅", "FAIL": "❌", "SKIP": "⏭️"}
summary = [
    f"### Test results: {passed} passed, {failed} failed, {skipped} skipped",
    "",
    "| Test | Result | Time (s) | Failure message |",
    "|---|---|---|---|",
]
for name, status, seconds, message in rows:
    summary.append(f"| {name} | {icons.get(status, status)} | {seconds:.1f} | {message.replace('|', '/')} |")

summary_file = os.environ.get("GITHUB_STEP_SUMMARY")
if summary_file:
    with open(summary_file, "a", encoding="utf-8") as f:
        f.write("\n".join(summary) + "\n")

print(f"::notice::{passed} passed, {failed} failed, {skipped} skipped")
for name, status, seconds, message in rows:
    if status == "FAIL":
        print(f"::error title={name}::{message}")
    elif status == "SKIP":
        print(f"::warning title={name} skipped::{message}")
