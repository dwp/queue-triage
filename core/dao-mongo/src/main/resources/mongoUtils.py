import argparse
import getpass
import subprocess


class PasswordAction(argparse.Action):
    def __call__(self, parser, namespace, values, option_string=None):
        if values is None:
            values = getpass.getpass('%s:' % self.dest)
        setattr(namespace, self.dest, values)

def execute_mongo_command(options, mongo_cmd):
    mongo_address = '%s/%s' % (options.dbAddress, options.appDb)
    mongo_exe = [
        'mongo',
        '--username=%s' % options.authUser,
        '--password=%s' % options.authPassword,
        '--authenticationDatabase=%s' % options.authDb,
        mongo_address
    ]

    print "Connecting to %s as %s" % (mongo_address, options.authUser)

    process = subprocess.Popen(mongo_exe,stdout=subprocess.PIPE,stdin=subprocess.PIPE)
    (out,err) = process.communicate(mongo_cmd)

    print out

def create_default_argument_parser():
    parser = argparse.ArgumentParser(formatter_class=argparse.ArgumentDefaultsHelpFormatter)
    parser.add_argument('--authUser', help='username for authentication', default='admin')
    parser.add_argument('--authPassword', nargs='?', help='password for authentication', action=PasswordAction, default='Passw0rd')
    parser.add_argument('--authDb', help='database for authentication', default='admin')
    parser.add_argument('--dbAddress', help='database address', default='localhost:27017')
    parser.add_argument('--appDb', help='database for the queue-triage application', default='queue-triage')
    return parser
