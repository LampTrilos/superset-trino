import logging
import jwt
from superset.security import SupersetSecurityManager

# Configure logging (usually done once, not within a class)
logging.basicConfig(level=logging.INFO, format='%(asctime)s - %(name)s - %(levelname)s - %(message)s')


class CustomSsoSecurityManager(SupersetSecurityManager):
    def oauth_user_info(self, provider, response=None):  # noqa: ARG002
        #This makes an http call that cannot receive the realm roles, only the dedicated client roles
        me = self.appbuilder.sm.oauth_remotes[provider].get("openid-connect/userinfo")
        me.raise_for_status()
        data = me.json()
        logging.info("User info from Keycloak: %s", data)
        #logging.info(data.get("realm_access", {}))
        #logging.info(data.get("realm_access", {}).get("roles", []))
        #To get the realm roles, we need to get them from the access token
        token = self.appbuilder.sm.oauth_remotes[provider].token
        #logging.info("Token is: %s", token)
        access_token = token.get("access_token")

        # Decode the access token
        decoded_token = jwt.decode(access_token, options={"verify_signature": False})
        roles = decoded_token.get("realm_access", {}).get("roles", [])
        # Remove the role "default-roles-quarkus"
        filtered_roles = [role for role in roles if role != "default-roles-quarkus"]
        logging.info("Roles from access token are: %s", filtered_roles)
        # Add 'Gamma' to the filtered roles, else simple users cannot see anything
        #filtered_roles = filtered_roles + ['Gamma']
        #logging.info("Roles from access token are: %s", filtered_roles)
        return {
            "username": data.get("preferred_username", ""),
            "first_name": data.get("given_name", ""),
            "last_name": data.get("family_name", ""),
            "email": data.get("email", ""),
            #"role_keys": data.get("roles", []),
            #"role_keys": data.get("realm_access", {}).get("roles", []),
            "role_keys": filtered_roles
        }
