#!/bin/bash
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function error_exit {
    echo
    echo "$@"
    exit 1
}
trap "error_exit 'Received signal SIGHUP'" SIGHUP
trap "error_exit 'Received signal SIGINT'" SIGINT
trap "error_exit 'Received signal SIGTERM'" SIGTERM
shopt -s expand_aliases
alias die='error_exit "Error ${0}(@`echo $(( $LINENO - 1 ))`):"'

REPO_ROOT=`git rev-parse --show-toplevel`
APPHOST_FOLDER=$REPO_ROOT/apphost/apphost-cli/build/install/apphost-cli
SWAGGER_FOLDER=$REPO_ROOT/apphost/docs-bundle/build/install/docs-bundle

# check binary
if [ ! -d "$APPHOST_FOLDER" ]; then
  die "apphost-cli build drop ($APPHOST_FOLDER) not found, have you build the code?"
fi

# extract build information
git_prop_file=$APPHOST_FOLDER/git.properties
git_commit_id=`sed "/^\#/d" $git_prop_file | grep 'commit.id.abbrev' | tail -n 1 | cut -d "=" -f2-`
git_commit_message=`sed "/^\#/d" $git_prop_file | grep 'commit.message.short' | tail -n 1 | cut -d "=" -f2-`
git_branch=`sed "/^\#/d" $git_prop_file | grep 'branch' | tail -n 1 | cut -d "=" -f2-`

echo "Git information from build drop:"
echo "  commit   = $git_commit_id"
echo "  message  = $git_commit_message"
echo "  branch   = $git_branch"
echo
echo "Will build docker image from the drop..."

while getopts ':y' flag; do
  case "${flag}" in
    y) confirmflag='true' ;;
    ?) ;;
  esac
done

if [ "$confirmflag" != "true" ]; then
  while true; do
    read -p "Are you sure to continue? [y/n] " REPLY
    if [[ $REPLY =~ ^[Nn]$ ]]; then
      die "cancelled by user"
    fi
    if [[ $REPLY =~ ^[Yy]$ ]]; then
      break
    fi
  done
fi

# copy binaries to current folder
echo "copying binaries..."
rm -r -f $DIR/bin
mkdir -p $DIR/bin/apphost
cp -r -f -p $APPHOST_FOLDER/* $DIR/bin/apphost/
rm -r -f $DIR/bin/apphost/logs
rm -r -f $DIR/bin/apphost/activemq-data
# run docker build
echo "start building docker image..."
#TODO
