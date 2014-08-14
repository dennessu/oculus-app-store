This image can be used to launch a silkcloud onebox in local dev env, or serve as a regular onebox.

To build the image:
1. build the repository
2. run ./buildimage.sh

To publish:

```
sudo docker login
sudo docker push silkcloud/onebox-app
```

DockerHub page: https://registry.hub.docker.com/u/silkcloud/onebox-app/

How to run docker based onebox: https://github.com/junbo/main/blob/master/docs/devdocs/RunDockerBasedOnebox.md
