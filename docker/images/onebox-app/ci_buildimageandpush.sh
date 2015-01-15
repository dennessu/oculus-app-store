#!/bin/bash

# this script would build onebox-app image and auto push to docker hub
# this should be run in CI environment, and the following env vars are required:
#  - DH_EMAIL
#  - DH_PASSWORD
#  - DH_USERNAME
set -e

DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"

function error_exit {
    echo
    echo "$@"
    exit 1
}
trap "error_exit 'Error happened, failed to build image'" ERR
shopt -s expand_aliases
alias die='error_exit "Error ${0}(@`echo $(( $LINENO - 1 ))`):"'

hash docker >/dev/null 2>&1 || die "!! docker not installed, cannot continue"

# hack: since shippable encrypted env var feature is not working, set the value here
DH_EMAIL=jubot@silkcloud.com
DH_PASSWORD="987#Bugsfor$abc"
DH_USERNAME=scdockerro

: ${DH_EMAIL:? "Env var DH_EMAIL not found, cannot continue"}
: ${DH_PASSWORD:? "Env var DH_PASSWORD not found, cannot continue"}
: ${DH_USERNAME:? "Env var DH_USERNAME not found, cannot continue"}

docker login -e "$DH_EMAIL" -p "$DH_PASSWORD" -u "$DH_USERNAME" || die "!! docker login failed"

REPO_ROOT=`git rev-parse --show-toplevel`
APPHOST_FOLDER=$REPO_ROOT/apphost/apphost-cli/build/install/apphost-cli

# check binary
if [ ! -d "$APPHOST_FOLDER" ]; then
  die "apphost-cli build drop ($APPHOST_FOLDER) not found, have you build the code?"
fi

# extract build information
git_prop_file=$APPHOST_FOLDER/git.properties
git_commit_id=`sed "/^\#/d" $git_prop_file | grep 'commit.id.abbrev' | tail -n 1 | cut -d "=" -f2-`
git_commit_message=`sed "/^\#/d" $git_prop_file | grep 'commit.message.short' | tail -n 1 | cut -d "=" -f2-`
git_branch=${git_branch:=`sed "/^\#/d" $git_prop_file | grep 'branch' | tail -n 1 | cut -d "=" -f2-`}
git_commit_date=`sed "/^\#/d" $git_prop_file | grep 'commit.time' | tail -n 1 | cut -d "=" -f2 | cut -d " " -f1 | sed "s/-//g"`
alternative_tag="$git_branch-$git_commit_date-$git_commit_id"

echo "Git information from build drop:"
echo "  commit   = $git_commit_id"
echo "  message  = $git_commit_message"
echo "  branch   = $git_branch"
echo
echo "Will build docker image from the drop($APPHOST_FOLDER)"
echo "Tags will be '$git_branch, $alternative_tag'"

# copy binaries to current folder
echo "## copying binaries..."
rm -r -f $DIR/bin
mkdir -p $DIR/bin/apphost
cp -r -f -p $APPHOST_FOLDER/* $DIR/bin/apphost/
rm -r -f $DIR/bin/apphost/logs
rm -r -f $DIR/bin/apphost/activemq-data
# run docker build
echo "## building docker image, set the tag to $git_branch"
docker build --rm -t silkcloud/onebox-app:$git_branch .
echo "## also set tag to $alternative_tag"
docker tag -f silkcloud/onebox-app:$git_branch silkcloud/onebox-app:$alternative_tag
echo "## finished building docker image"
echo "## now pushing docker image to docker hub"
docker push silkcloud/onebox-app:$git_branch
docker push silkcloud/onebox-app:$alternative_tag
echo "## docker images pushed to docker hub, tags = silkcloud/onebox-app:$git_branch silkcloud/onebox-app:$alternative_tag"
