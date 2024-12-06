package org.linuxfoundation.demos;

import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.apps.DeploymentBuilder;
import io.javaoperatorsdk.operator.api.reconciler.Context;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.CRUDKubernetesDependentResource;
import io.javaoperatorsdk.operator.processing.dependent.kubernetes.KubernetesDependent;
import io.k8s.samplecontroller.v1alpha1.Foo;

@KubernetesDependent(labelSelector = "app.kubernetes.io/managed-by=sample-operator")
public class DeploymentDependentResource extends CRUDKubernetesDependentResource<Deployment, Foo> {
  public DeploymentDependentResource() {
    super(Deployment.class);
  }

  @Override
  protected Deployment desired(Foo foo, Context<Foo> context) {
    final ObjectMeta fooMetadata = foo.getMetadata();
    final String fooName = fooMetadata.getName();
    return new DeploymentBuilder()
      .withNewMetadata()
      .withName(fooName)
      .withNamespace(fooMetadata.getNamespace())
      .addToLabels("app", fooName)
      .addToLabels("app.kubernetes.io/part-of", fooName)
      .addToLabels("app.kubernetes.io/managed-by", "tomcat-operator")
      .endMetadata()
      .withNewSpec()
      .withNewSelector().addToMatchLabels("app", fooName).endSelector()
      .withReplicas(foo.getSpec().getReplicas().intValue())
      // set tomcat version
      .withNewTemplate()
      // make sure label selector matches label (which has to be matched by service selector
      // too)
      .withNewMetadata().addToLabels("app", fooName).endMetadata()
      .withNewSpec()
      .addNewContainer()
      .withName("nginx")
      .withImage("nginx:latest").endContainer()
      .endSpec()
      .endTemplate()
      .endSpec()
      .build();
  }
}
