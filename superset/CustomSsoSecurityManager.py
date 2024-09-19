import logging
from superset.security import SupersetSecurityManager

# Configure logging (usually done once, not within a class)
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')


class CustomSsoSecurityManager(SupersetSecurityManager):
    def oauth_user_info(self, provider, response=None):  # noqa: ARG002
        me = self.appbuilder.sm.oauth_remotes[provider].get("openid-connect/userinfo")
        me.raise_for_status()
        data = me.json()
        logging.info("User info from Keycloak: %s", data)
        #logging.info(data.get("realm_access", {}).get("roles", []))
        return {
            "username": data.get("preferred_username", ""),
            "first_name": data.get("given_name", ""),
            "last_name": data.get("family_name", ""),
            "email": data.get("email", ""),
            #"role_keys": data.get("roles", []),
            "role_keys": data.get("realm_access", {}).get("roles", []),
        }
