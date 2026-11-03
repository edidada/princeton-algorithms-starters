"""Remote-only checks of the actual starter-assignment command-line programs."""
import collections
import json
import os
from pathlib import Path
import subprocess

ROOT = Path(__file__).resolve().parents[1]
LAB = ROOT / 'hello'
CP = os.pathsep.join([str(LAB / 'target/classes'), str(LAB / '.lift/algs4.jar')])


def run(name, args=(), text=''):
    return subprocess.run(['java', '-cp', CP, name, *args], input=text, text=True,
                          capture_output=True, check=True, timeout=20).stdout


assert run('HelloWorld') == 'Hello, World\n'
for a, b in [('Kevin', 'Bob'), ('Alejandra', 'Bahati')]:
    assert run('HelloGoodbye', (a, b)) == f'Hello {a} and {b}.\nGoodbye {b} and {a}.\n'
assert run('RandomWord', text='') == '\n'
assert run('RandomWord', text='only') == 'only\n'
assert run('RandomWord', text='echo ' * 100) == 'echo\n'
long_words = [f'word{i}' for i in range(20000)]
assert run('RandomWord', text='\t\n'.join(long_words)).strip() in set(long_words)
counts = collections.Counter(run('RandomWord', text='first middle last').strip() for _ in range(120))
assert set(counts) == {'first', 'middle', 'last'}
# This observes randomized selection; uniformity follows from the reservoir proof.
output = dict(cli_cases=127, random_samples=120, observed=dict(counts),
              caveat='frequency observations are not a proof of uniformity')
(LAB / 'target/checks.json').write_text(json.dumps(output, indent=2) + '\n')
print(json.dumps(output, indent=2))
