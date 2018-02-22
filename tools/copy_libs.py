from __future__ import print_function
from os import makedirs, path
from shutil import rmtree, copyfile

import sys

if len(sys.argv) < 3:
    print('Usage:')
    print('    %s <out> <lib>...' % sys.argv[0], file=sys.stderr)
    print('Parameters:', file=sys.stderr)
    print('    out    Output path to write jars', file=sys.stderr)
    print('    lib    libraries to copy to <out>', file=sys.stderr)
    exit(1)

out = sys.argv[1]
lib = sys.argv[2:]

jars = set()

def prune(lib):
    return [jar for entry in lib for jar in entry.split(":")]

def make_clean_dir(dir):
    if path.isdir(dir):
        rmtree(dir)
    makedirs(dir)

def copy_jars(libs, directory):
    make_clean_dir(path.join(directory))
    for j in libs:
        jar = path.join(directory, path.basename(j))
        if jar not in jars:
            jars.add(jar)
            copyfile(j, jar)

if lib:
    copy_jars(prune(lib), path.join(out, 'lib'))
