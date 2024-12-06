package org.linuxfoundation.demos;

import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.client.utils.Serialization;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.api.reconciler.ControllerConfiguration;
import io.javaoperatorsdk.operator.api.reconciler.Reconciler;
import io.javaoperatorsdk.operator.api.reconciler.UpdateControl;
import io.javaoperatorsdk.operator.api.reconciler.dependent.Dependent;
import io.k8s.samplecontroller.v1alpha1.Foo;

import io.k8s.samplecontroller.v1alpha1.FooStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ControllerConfiguration(
  dependents = {
    @Dependent(type = DeploymentDependentResource.class)
  })
public class FooReconciler implements Reconciler<Foo> {
  private static final Logger logger = LoggerFactory.getLogger(FooReconciler.class.getName());

  @Override
  public UpdateControl<Foo> reconcile(final Foo foo, Context<Foo> context) throws Exception {
   return context.getSecondaryResource(Deployment.class).map(deployment -> {
     Foo updatedFoo = updateAvailableReplicasInFooStatus(foo, deployment.getSpec().getReplicas());
     logger.info("Updating status of Foo {} in namespace {} to {} ready replicas",
       foo.getMetadata().getName(),
       foo.getMetadata().getNamespace(),
       foo.getSpec().getReplicas());
     return UpdateControl.patchStatus(updatedFoo);
   }).orElseGet(UpdateControl::noUpdate);
  }

  private Foo updateAvailableReplicasInFooStatus(Foo foo, long replicas) {
    FooStatus fooStatus = new FooStatus();
    fooStatus.setAvailableReplicas(replicas);
    // NEVER modify objects from the store. It's a read-only, local cache.
    // You can create a copy manually and modify it
    Foo fooClone = Serialization.clone(foo);
    fooClone.setStatus(fooStatus);
    return fooClone;
  }
}
