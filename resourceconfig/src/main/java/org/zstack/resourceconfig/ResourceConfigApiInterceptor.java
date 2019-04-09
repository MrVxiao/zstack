package org.zstack.resourceconfig;

import org.springframework.beans.factory.annotation.Autowire;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Configurable;
import org.zstack.core.config.GlobalConfig;
import org.zstack.core.config.GlobalConfigFacade;
import org.zstack.header.apimediator.ApiMessageInterceptionException;
import org.zstack.header.apimediator.ApiMessageInterceptor;
import org.zstack.header.message.APIMessage;
import org.zstack.identity.AccountManager;
import org.zstack.identity.rbac.CheckIfAccountCanAccessResource;

import java.util.Collections;

import static org.zstack.core.Platform.argerr;

@Configurable(preConstruction = true, autowire = Autowire.BY_TYPE)
public class ResourceConfigApiInterceptor implements ApiMessageInterceptor {
    @Autowired
    private GlobalConfigFacade gcf;
    @Autowired
    private ResourceConfigFacade rcf;
    @Autowired
    private AccountManager acMgr;

    @Override
    public APIMessage intercept(APIMessage msg) throws ApiMessageInterceptionException {
        if (msg instanceof ResourceConfigMessage) {
            validate((ResourceConfigMessage) msg);
        }

        if (msg instanceof APIGetResourceConfigMsg) {
            validate((APIGetResourceConfigMsg) msg);
        }
        return msg;
    }

    private void validate(ResourceConfigMessage msg) {
        GlobalConfig gc = gcf.getAllConfig().get(msg.getIdentity());
        if (gc == null) {
            throw new ApiMessageInterceptionException(argerr("no global config[category:%s, name:%s] found",
                    msg.getCategory(), msg.getName()));
        }

        ResourceConfig rc = rcf.getResourceConfig(gc.getIdentity());
        if (rc == null) {
            throw new ApiMessageInterceptionException(argerr("global config[category:%s, name:%s] cannot bind resource",
                    msg.getCategory(), msg.getName()));
        }
    }

    private void validate(APIGetResourceConfigMsg msg) {
        if (acMgr.isAdmin(msg.getSession())) {
            return;
        }

        if (!CheckIfAccountCanAccessResource.check(Collections.singletonList(msg.getResourceUuid()), msg.getSession().getAccountUuid()).isEmpty()) {
            throw new ApiMessageInterceptionException(argerr("account has no access to the resource[uuid: %s]",
                    msg.getSession().getAccountUuid(), msg.getResourceUuid()));
        }
    }
}
