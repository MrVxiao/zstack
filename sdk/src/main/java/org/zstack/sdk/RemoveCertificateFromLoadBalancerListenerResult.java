package org.zstack.sdk;

import org.zstack.sdk.LoadBalancerListenerInventory;

public class RemoveCertificateFromLoadBalancerListenerResult {
    public LoadBalancerListenerInventory inventory;
    public void setInventory(LoadBalancerListenerInventory inventory) {
        this.inventory = inventory;
    }
    public LoadBalancerListenerInventory getInventory() {
        return this.inventory;
    }

}