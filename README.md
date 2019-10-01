# epic-cron
Spring Boot cron functions for riff v0.1.3 or later

riff environment
----------------------

Please consult the official documentation in order to setup riff in the appropriate environment

  * Google Kubernetes Engine: https://projectriff.io/docs/v0.5/getting-started/gke
  * Docker Community Edition for Windows: https://projectriff.io/docs/v0.5/getting-started/docker-for-windows

Development
----------------------
You can call your cron jobs as soon as the Spring Boot app is started for development and testing purposes by specifying 
the appropriate `<bean-name>` to be called with the given `<argument>`:

     mvn spring-boot:run
     curl localhost:8090/<bean-name> -H 'Content-Type: text/plain' -w '\n' -d <argument>

Be sure to either send the argument, or omit the `-d` flag completely, according to the signature of the functional 
interface that should respond. If the signature is not matched, you will get `404 NOT FOUND`.

More information here: https://projectriff.io/docs/v0.4/invokers/java

Creation
----------------------

First, create a local docker repository where to push function images:

    docker run -d -p 5000:5000 --restart=always --name registry registry:2
    
Don't forget to mark the local repository as insecure to .docker/daemon.json or else you won't be able to pull images:

    { "insecure-registries":["<repository-address>"] }

Afterward, create a function from the code:

     riff function create <function-name> --handler <bean-name> --local-path . --image <repository-address>/monaco-telecom/<function-name> --tail
     
NOTES: 
 * You may need to set `<repository-address>` to the ip address of localhost, port 5000, to be able to connect (eg: 172.16.30.174:5000)
 * The `--local-path` flag is disabled on Windows; you can use `--git-repo` to create a function from some git code but _gitlab.prod.lan_ is not working without the TLS certificate, while github does.

Deployment
----------------------
In the following script knative is chosen as deployer to kubernetes for the image that we previously built. 

    riff knative deployer create <deployer-name> --image <repository-address>/monaco-telecom/<function-name>:latest --tail
    
Then, retrieve the host where the function is deployed to:

     $ riff knative deployer list
     NAME                  TYPE    REF                                                            HOST                  STATUS   AGE
     <deployer-name>       image   <repository-address>/monaco-telecom/<function-name>:latest     <deployer-host>       Ready    8m5s

Lastly, to invoke the function with a given `<argument>` on the cluster located at `<istio-address>` use the following script:
    
    curl http://<istio-address>/ -H 'Host: <deployer-host>' -H 'Content-Type: application/json' -d <argument>

Running a function in a cron job
-----------------------

Prepare a file named `<function-name>-job` with the following definition; tweak the schedule so that it match your needs.
It is possible to deploy it with the command `kubectl apply --filename <function-name>-job`

     apiVersion: batch/v1beta1
     kind: CronJob
     metadata:
       name: <function-name>-job
     spec:
       schedule: "*/1 * * * *"
       jobTemplate:
         spec:
           template:
             spec:
               containers:
               - name: <function-name>-job
                 image: buildpack-deps:curl
                 args:
                 - /bin/sh
                 - -c
                 - "curl http://<istio-address>/ -H 'Host: <deployer-host>' -H 'Content-Type: application/json' -d <argument>"
               restartPolicy: OnFailure

 Further information can be found here: https://kubernetes.io/docs/tasks/job/automated-tasks-with-cron-jobs/
 
 Cleanup
 -----------------------
 
 In order to delete the function `<function-name>`, the corresponding deployer `<deployer-name>` and the cron job execute the 
 following commands:
 
      riff function delete <function-name>
      riff knative deployer delete <deployer-name>
      kubectl delete cronjob <function-name>-job