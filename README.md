# sample-operator

This project is a java port of [Kubernetes Sample Controller](https://github.com/kubernetes/sample-controller) using [Java Operator SDK](https://github.com/operator-framework/java-operator-sdk).

This repository implements a simple controller for watching Foo resources as defined with a CustomResourceDefinition (CRD).

This particular example demonstrates how to perform basic operations such as:

- How to register a reconciler for new custom resource (custom resource type) of type Foo using `io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration` annotation.
- How to create/get/list instances of your new resource type Foo.
- How to make other resources depend on custom resource using `io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent` annotation.

## How to Run?

**Prerequisite:** Since the sample-controller uses apps/v1 deployments, the Kubernetes cluster version should be greater than 1.9.
```shell
# Install Custom Resource Definition
kubectl create -f src/main/resources/crd/foo-crd.yaml
# Install ClusterRole, ClusterRoleBinding and ServiceAccount for Operator to work with
kubectl create -f src/main/resources/foo-serviceaccount-and-role-binding.yml
# Deploy Operator to Kubernetes cluster using Kubernetes Maven Plugin
# (Optional) To point your shell to minikube's docker-daemon, run:
eval $(minikube -p minikube docker-env)
mvn package k8s:build k8s:resource k8s:apply
```
Once Operator has been deployed to Cluster, check for pods (there should be one named `sample-operator` running:
```shell
kubectl get pods
```

Create an instance of `Foo` resource:
```shell
kubectl create -f src/main/resources/example-foo.yaml
foo.samplecontroller.k8s.io/example-foo created
```
You'd notice that Operator detected this change and created the dependent resource Deployment for this `example-foo` resource.
