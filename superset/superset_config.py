from flask_appbuilder.security.manager import AUTH_OAUTH

SECRET_KEY='TBLKfWlPWmnck079U09c9mOeoZnoAEdF'

#The below class is needed because Superset omits the roles when fetching from Keycloak
# Custom Security Manager class
# class CustomSsoSecurityManager(SupersetSecurityManager):
#     def oauth_user_info(self, provider, response=None):  # noqa: ARG002
#         me = self.appbuilder.sm.oauth_remotes[provider].get("openid-connect/userinfo")
#         me.raise_for_status()
#         data = me.json()
#         logging.info("User info from Keycloak: %s", data)
#         return {
#             "username": data.get("preferred_username", ""),
#             "first_name": data.get("given_name", ""),
#             "last_name": data.get("family_name", ""),
#             "email": data.get("email", ""),
#             "role_keys": data.get("groups", []),
#         }

# Import CustomSsoSecurityManager from the same directory
from CustomSsoSecurityManager import CustomSsoSecurityManager
# Set the custom security manager
CUSTOM_SECURITY_MANAGER = CustomSsoSecurityManager



# Enable OAuth authentication
AUTH_TYPE = AUTH_OAUTH
#LOGOUT_REDIRECT_URL='http://host.docker.internal:8090/realms/quarkus/protocol/openid-connect/logout'
LOGOUT_REDIRECT_URL='http://localhost:8088/login/'
#With this a successfull login via Keycloak creates a user with the  role given by Keycloak
AUTH_USER_REGISTRATION = True
#AUTH_USER_REGISTRATION_ROLE = 'Gamma'
#To sync role at every login
AUTH_ROLES_SYNC_AT_LOGIN = True

# OAuth provider configuration for Keycloak
OAUTH_PROVIDERS = [
    {
        'name': 'keycloak',
        'icon': 'fa-key',
        'token_key': 'access_token',  # Keycloak uses 'access_token' for the access token
        'remote_app': {
            'client_id': 'backend-service',
            'client_secret': 'secret',
            'client_kwargs': {
                'scope': 'openid email profile',
                #'scope': 'openid profile email  roles',
            },
            'server_metadata_url': 'http://host.docker.internal:8090/realms/quarkus/.well-known/openid-configuration',
            'api_base_url': 'http://host.docker.internal:8090/realms/quarkus/protocol/',
            'access_token_url': 'http://host.docker.internal:8090/realms/quarkus/protocol/openid-connect/token',
            'authorize_url': 'http://host.docker.internal:8090/realms/quarkus/protocol/openid-connect/auth',
        },
    }
    ]
#Mapping of Keycloak roles to Superset roles
AUTH_ROLES_MAPPING = {
  "admin": ["Admin"],
  "superUser": ["Alpha"],
  "simpleUser": ["Gamma"],
  "sales": ["sales"],
  "logistics": ["logistics"]
}


