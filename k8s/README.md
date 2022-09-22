# k8s

This project contains the one-time initialization of k8s namespace(s) for
the serivces-inkijk project.

## rancher/setup
Yaml files that need to be run manually one time only.
- `gitlab-admin-service-account.yml` 
- `ammipa01-root-ca.yml` 
  
  Already has been run for `bvv` project, just added to be complete.
- `security-policy.yml`  
- `tracing.yml`

  Optional: set zipkin tracing to 100%

## namespaces
These directories contain the namespace specific yaml files. One file needs
to be run manually once, the others can be activated through the pipeline:

- `sa-gitlab-deployer`
 
  Service account (and secret) that gitlab uses to connect to the cluster.
