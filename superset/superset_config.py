from flask_appbuilder.security.manager import AUTH_OAUTH

SECRET_KEY='TBLKfWlPWmnck079U09c9mOeoZnoAEdF'

# Enable OAuth authentication
AUTH_TYPE = AUTH_OAUTH
LOGOUT_REDIRECT_URL='http://keycloak:8090/realms/quarkus/protocol/openid-connect/logout'
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
                'scope': 'openid profile ivuser email roles',
            },
            'server_metadata_url': 'http://keycloak:8090/realms/quarkus/.well-known/openid-configuration',
            'api_base_url': 'http://keycloak:8090/realms/quarkus/protocol/',
        },
    }
    ]
