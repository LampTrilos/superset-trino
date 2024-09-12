from flask_appbuilder.security.manager import AUTH_OAUTH

SECRET_KEY='TBLKfWlPWmnck079U09c9mOeoZnoAEdF'

# Enable OAuth authentication
AUTH_TYPE = AUTH_OAUTH
#LOGOUT_REDIRECT_URL='http://host.docker.internal:8090/realms/quarkus/protocol/openid-connect/logout'
LOGOUT_REDIRECT_URL='http://localhost:8088/login/'
AUTH_USER_REGISTRATION = True
AUTH_USER_REGISTRATION_ROLE = 'Gamma'
# OAuth provider configuration for Keycloak
OAUTH_PROVIDERS = [
    {
        'name': 'keycloak',
        'icon': 'fa-key',
        'token_key': 'access_token',  # Keycloak uses 'access_token' for the access token
        'remote_app': {
            'client_id': 'superset',
            'client_secret': 'TBLKfWlPWmnck079U09c9mOeoZnoAEdF',
            'client_kwargs': {
                'scope': 'openid profile email ouid',
            },
            'server_metadata_url': 'http://host.docker.internal:8090/realms/quarkus/.well-known/openid-configuration',
            'api_base_url': 'http://host.docker.internal:8090/realms/quarkus/protocol/',
        },
    }
    ]
