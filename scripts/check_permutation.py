"""Exercise actual Java stdin/stdout client, not a reimplementation."""
import os
from pathlib import Path
import subprocess
import collections

classpath = os.pathsep.join(['queues/target/classes', 'queues/.lift/algs4.jar'])
for k, words in [(0, []), (0, ['a','b']), (4, ['a','b','c','d']), (3, ['a']*10), (7, [str(i) for i in range(100)])]:
    result = subprocess.run(['java','-cp',classpath,'Permutation',str(k)],
                            input=' '.join(words),text=True,capture_output=True,check=True)
    values = result.stdout.split()
    assert len(values)==k
    assert not (collections.Counter(values)-collections.Counter(words))
bad = subprocess.run(['java','-cp',classpath,'Permutation','3'],input='a b',text=True,capture_output=True)
assert bad.returncode != 0
print('Permutation stdin/stdout boundaries, duplicates, counts and membership passed')
