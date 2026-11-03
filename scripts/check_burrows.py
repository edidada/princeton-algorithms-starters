"""Remote-only binary CLI oracle, round trips, and compression measurements."""
from pathlib import Path
import json
import os
import random
import struct
import subprocess
import time

ROOT = Path(__file__).resolve().parents[1]
LAB = ROOT / 'burrows'
CP = os.pathsep.join([str(LAB / 'target/classes'), str(LAB / '.lift/algs4.jar')])


def run(program, mode, data):
    return subprocess.run(['java', '-Xmx1g', '-cp', CP, program, mode], input=data,
                          stdout=subprocess.PIPE, stderr=subprocess.PIPE,
                          check=True, timeout=90).stdout


def mtf(data):
    alphabet = list(range(256))
    output = bytearray()
    for value in data:
        position = alphabet.index(value)
        output.append(position)
        alphabet.insert(0, alphabet.pop(position))
    return bytes(output)


def verify(data, small=False):
    encoded = run('BurrowsWheeler', '-', data)
    assert len(encoded) == len(data) + 4
    first, = struct.unpack('>i', encoded[:4])
    if not data:
        assert first == 0
    elif small:
        rotations = sorted(data[i:] + data[:i] for i in range(len(data)))
        assert rotations[first] == data
        assert encoded[4:] == bytes(row[-1] for row in rotations)
    assert run('BurrowsWheeler', '+', encoded) == data
    moved = run('MoveToFront', '-', data)
    assert moved == mtf(data)
    assert run('MoveToFront', '+', moved) == data


randomizer = random.Random(1103)
cases = [b'', b'A', b'A' * 70, b'AB' * 31, b'ABRACADABRA!', bytes(range(256)),
         b'\x00\xff\x80\x00\xff', randomizer.randbytes(1024)]
for data in cases:
    verify(data, len(data) <= 256)
assert run('BurrowsWheeler', '-', b'ABRACADABRA!') == bytes.fromhex('00000003415244215243414141414242')
assert run('MoveToFront', '-', b'ABRACADABRA!') == bytes.fromhex('414252024401450104040226')
for name in ['abra.txt', 'aesop.txt', 'us.gif']:
    original = (LAB / name).read_bytes()
    assert run('BurrowsWheeler', '-', original) == (LAB / (name + '.bwt')).read_bytes()
    assert run('MoveToFront', '-', original) == (LAB / (name + '.mtf')).read_bytes()
verify(randomizer.randbytes(1_000_000))
measurements = []
for name in ['aesop.txt', 'CS_bricks.jpg', 'rand10K.bin']:
    original = (LAB / name).read_bytes()
    start = time.perf_counter()
    transformed = run('BurrowsWheeler', '-', original)
    moved = run('MoveToFront', '-', transformed)
    compressed = run('edu.princeton.cs.algs4.Huffman', '-', moved)
    compression_seconds = time.perf_counter() - start
    start = time.perf_counter()
    unmoved = run('edu.princeton.cs.algs4.Huffman', '+', compressed)
    reversed_transform = run('MoveToFront', '+', unmoved)
    restored = run('BurrowsWheeler', '+', reversed_transform)
    expansion_seconds = time.perf_counter() - start
    assert restored == original
    measurements.append(dict(file=name, original_bytes=len(original), compressed_bytes=len(compressed),
                             ratio=len(compressed) / len(original), compress_seconds=compression_seconds,
                             expand_seconds=expansion_seconds))
output = dict(binary_cases=len(cases) + 1, reference_fixtures=3,
              timing_scope='three separate JVM invocations per direction, including startup',
              measurements=measurements)
(LAB / 'target/compression.json').write_text(json.dumps(output, indent=2) + '\n')
print(json.dumps(output, indent=2))
