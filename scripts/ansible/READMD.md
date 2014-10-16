## Silkcloud Ansible playbooks

### How to test locally
You can install ansible locally to do some verification during development.

If you are using mac, just run `brew install ansible`.

You can use --list-hosts or --check-syntax to verify your script, like:
```
ansible -i int all --list-hosts --check-syntax
ansible-playbook -i stage site.yml --check-syntax
```

You can also run check-syntax.sh to check all yml file syntax
```
./check-syntax.sh
```
