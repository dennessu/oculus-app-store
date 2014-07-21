# Git Setup Guide

## Install Git
Check your git version by:
```
git --version
```
Make sure the git version is `1.9.x` or above. If your git version is incorrect, please install the latest git.
  * **Windows**: Download the git from [Git for Windows](http://msysgit.github.io)
  * **OS X**: OS X has a default git, which is 1.8.x. Install [Home Brew](http://brew.sh) and then install
  ```
  brew install git
  ```
  Note: this may install git 2.0 for you. It should work but I haven't tried.

Make sure git is in your `PATH`.

## Git Config

### Set User Name
Please use your silkcloud alias as username, and silkcloud email as email. This can help phabricator to recognize you.
Assume your user name is `abcd` and your email is `abcd@silkcloud.com`.

```
git config --global user.name abcd
git config --global user.email abcd@silkcloud.com
```

You may not want to do this in global level, because you may have personal github projects that are not related to silkcloud.
So instead, you can config it at repository level.

```
cd $SILKCLOUD_REPO
git config user.name abcd
git config user.email abcd@silkcloud.com
```

### Default to Rebase instead of Merge
It is very helpful if we can avoid introducing a lot of merging commits, to make sure we have a clean history. Please use phabricator as your dev workflow, and config your git as following:
```
git config --global branch.autosetuprebase always
git config --global pull.rebase true
```

To understand the details about git rebase, refer to [Git Branching - Rebasing](http://git-scm.com/book/en/Git-Branching-Rebasing) from Git Pro.

**Warning**: **NEVER NEVER NEVER** use `git push --force` to change commit history which is already pushed. You are doing something wrong! STOP!

**IMPORTANT**: if you have used pull request before and set `origin` to **your own github repo**, please set `origin` back to `junbo/main`. For example, if your alias is abcd, the following command will rename the remote from `origin` to `abcd`, and add `junbo/main` as `origin`.
```
git remote rename origin abcd
git remote add origin git@github.com:junbo/main.git
git branch master -u origin/master
```

### Other Git Configs
Run the following commands for git configurations.
```
git config --global push.default simple
git config --global merge.tool kdiff3
git config --global rerere.enabled true
```
We use `kdiff3` as diff tool. Please [download](http://sourceforge.net/projects/kdiff3/files/) and install it to default location. For more information about kdiff3 in git, refer to [this article](http://naleid.com/blog/2012/01/12/how-to-use-kdiff3-as-a-3-way-merge-tool-with-mercurial-git-and-tower-app).

### Consolidate CR/LF Behavior
The biggest headache of cross platform git, is the line ending issue. Since our production environment is linux, it is recommend that we always commit as LF, instead of CRLF.
To make sure you don't introduce wrong line ending from your windows client, please do the following:

#### Windows Specific Config
In windows, specify auto CRLF.
```
git config --global --unset core.eol
git config --global core.autocrlf true
```

#### OS X/Linux Specific Config
We should also make sure we don't commit CRLF from Mac/Linux, this can happen if we copy paste some code from internet.

```
git config --global --unset core.eol
git config --global core.autocrlf input
```

## Setup Arcanist

### Setup Arcanist on Windows
Install arcanist on windows is relatively difficult, you can follow the [official document](https://secure.phabricator.com/book/phabricator/article/arcanist_windows/) (not recommended).

Or, we have a simplified instruction:

  1. Download [this file](https://code.silkcloud.info/file/info/PHID-FILE-xyvmgadhminoewlh7q7r/)
  1. Extract it to `c:\arc` (**please** use this path, otherwise you need to find and replace in some files)
  1. Add `c:\arc\arcanist\bin` to your `%PATH%` environment variable
  1. Open a new terminal, make sure `arc --help` can work
  1. `cd` to your repository (`junbo/main` for example), execute
  ```
  c:\silkcloudcode\main> arc list
  ```
  You would get the error, saying that you don't have the phabricator certificate, now let's install the cert.
  Execute the following command, follow the instruction to input the token you got from the website
  ```
  c:\silkcloudcode\main> arc install-certificate
  ```
  Type `arc list` to confirm. If there is no error, you should be all set.

**Warning**: If you have `php.exe` previously in your `%PATH%`, please remove it or make sure `c:\arc\arcanist\bin` is ahead of other `php.exe` path. Otherwise, you will get some cert error.

**Note**: If you are curious about how to the package is built, check it out [here](https://code.silkcloud.info/w/build_arcanist_package_for_windows/).

### Setup Arcanist on OS X/Linux

1. OS X has php installed by default, so php setup can be skipped.
1. Our default arcanist setting uses `vim` as editor, make sure `vim` is available in your terminal. If not, `brew install vim` or `apt-get install vim`
1. Create a directory to store arcanist and libphutil, like `~/arc`
1. Clone the repositories:
```
git clone git://github.com/facebook/libphutil.git
git clone git://github.com/facebook/arcanist.git
```
1. Add binary to `$PATH. Add the following line to your `~/.bash_profile` (bash) or `~/.zshenv` (zsh)
```
export PATH=$PATH:$HOME/arc/arcanist/bin
```
1. Reopen a terminal, you should be able to execute `arc` now. Execute `arc --help` in your terminal to confirm.
1. Now **cd to your silkcloud repository**, then execute
```
arc list
```
1. You would get the error, saying that you don't have a phabricator certificate, now let's install the cert
1. Execute the following command, follow the instruction to input the token you got from the web site
```
install-certificate
```
1. Type `arc list` to confirm. If there is no error, you should be all set.

## Dev Workflow
We use git as source control system and phabricator as code review system.
Phabricator also contains a good wiki, an issue tracking system, a nice repo viewer and a lot of nice dev tool. [This](https://code.silkcloud.info/w/why_phabricator/) can tell you why we should use phabricator.
Also, [here](https://code.silkcloud.info/w/phabricator_simple_guide/) is a simple guide of phabricator if you are not familiar with it.

There are quite a few git workflows in the world, see [Git Workflows](https://www.atlassian.com/git/workflows).
We want linear git commit history and 'Centralized Workflow', arcanist/phabricator are designed to work in that style.

Typically, the workflow is like this:
```
+-------------------+          +---------------------------+      +-------------------------+
|Decide to work on a|          |create a local topic branch|      |Change code, do test.    |
|bug or a feature   +---------->$ arc feature branchName   +------>                         |
+-------------------+          +---------------------------+      +-----------+-------------+
                                                                              |
                                                                              |
                      +------------+--------------+               +-----------v--------------+
                      |Send code review.          |               |When you are ready to go. |
        +-------------+$ arc diff                 <---------------+commit your changes.      |
        |             |                           |               |$ git commit -m 'title'   |
        |             +--------------------^------+               +-------------+------------+
        |                                  |
        |                                  |
        |                                  |
+-------v-+---------------+                |      +------------------------------+
|Others review your code  |    Accepted    |      |Your change is good.          |
|on code.silkcloud.com    +----------------------->You can push it.              |
|                         |                |      |$ arc land                    |
+---------+---------------+                |      +------------------------------+
          |                                |
          |Rejected                        |
          |                                |
+---------v---------------+         +------+--------------------------+
|Your change is not so    |         |When you are ready.              |
|good, change according   +--------->Commit (amend) your changes.     |
|to code reView comments. |         |$ git commit --amend             |
|                         |         |                                 |
+-------------------------+         +---------------------------------+
```
Here is the detailed explanation:

1. `origin/master` is our default dev branch, we commit changes to it during development.
1. When you want to work on something. 'something' can be a bug, can be a feature, can be a workitem. But please remember that **every commit** you introduced, should be consider as a good change. **Never** push any commits that break things. In another word, every time you push your change, there should be **only 1 commit**.
1. Use `arc feature branch_name` to create a local topic branch. Don't commit to master branch locally.
1. Then change your code, do local test, until your change is ready to go.
1. Commit the change to your topic branch. `git add -A :/` then `git commit`
1. Send the code review to the team, specify some reviewers and CC someones. Run `arc diff`, input summary/reviewers/etc. For more arcanist help, read [official help doc](https://secure.phabricator.com/book/phabricator/article/arcanist/).
1. Wait for reviewers to approve or reject the change.
1. You can always to switch to other topic branch. `arc feature` to list your topic branches. `arc feature xxx` to create or checkout `xxx` branch. You can switch context easily, yes, you are enjoying the benefit of git.
1. You get some code review comments. You can discuss them on phabricator. When you decide to change accordingly, check out to your topic branch, change the code.
1. Before you send another code review iteration, you need to commit them. As we said before, introduce at most 1 commit for every push, so do this `git commit --amend`
1. `arc diff` again, you will send another iteration.
1. Repeat step 9 - 11, until your reviewers like your change.
1. Now you can push, use `arc land`, it would push the changes and delete the topic branch for you.

Here is a sample:
```
# You want to work on task 123, create a local topic branch
$ arc feature T123_fix_memory_leak

# change the code
$ vim yourcode.java
$ vim yourconfig.conf

# commit them
$ git add .
$ git commit -m "T123, implement feature XXX"

# write good commit message and send code review
$ arc diff

# someone gave you good suggestions, change accordingly
$ vim yourcode.java

# commit the changes, this time use --amend, so that there is only 1 commit
$ git commit --amend

# send another iteration
$ arc diff

# reviewer accepted your change, push the change.
$ arc land
```

Sometimes we need to work on some other remote branch other than master, like `release`. Here is how to do it:
```
# suppose you are working on something on 'release' branch

# specify start when you do arc feature
$ arc feature T899_fix_race_condition origin/release

# double confirm the branch information
$ git branch -vv

# make your changes and commit
$ vim; git add. ; git commit ....

# still use arc diff to send code review
$ arc diff

# specify --onto when you do arc land
$ arc land --onto release
```

**Here are some important rules, please remember them**
  * Always create topic branch locally, never commit to master.
  * (again) **NEVER NEVER NEVER** run `git push --force` !!! You are doing something wrong! STOP!
  * Never introduce merge commit (in dev branches). After you set the git config as above, `git pull` will do the work.
  * Write good commit messages. Title should be one line and descriptive. It should be no more than 67 characters and must be less than 80. Write as much information as possible in 'summary'. `arc diff` would populate the template which contains a couple fields. Please read this article: [Write Sensible Commit Messages](https://secure.phabricator.com/book/phabflavor/article/writing_reviewable_code/#write-sensible-commit-me)
  * Remember in every topic branch, introduce at most 1 commit. Use `git commit --amend` wisely.
    **NOTE**: Luckily, 'arc land' would help you to squash your commits in your topic branch before push, but it is still recommended that you keep your own stuffs clean and tidy.
  * Don't rely on IDE to do git operations. We might introduce git hooks in the future. Usually IDEs don't honor local git hooks. Try to learn git CLI.

# HOWTOs
1. How to sync my working branch to the latest status?
```
git pull
```
1.


# References
  * [Git Config Guide](https://silkcloud.atlassian.net/wiki/display/IN/Git+config+guide)
  * [Git Setup Guide](https://code.silkcloud.info/w/git_setup_guide/)
  * [Arcanist Setup Guide](https://code.silkcloud.info/w/arcanist_setup_guide/)
  * [Arcanist Dev Workflow](https://code.silkcloud.info/w/dev_workflow/)
