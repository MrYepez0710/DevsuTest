function fn() {
  var env = karate.env; // get system property 'karate.env'
  karate.log('karate.env system property was:', env);
  
  if (!env) {
    env = 'dev';
  }
  
  var config = {
    clientAppUrl: 'http://localhost:8080/api',
    transactionAppUrl: 'http://localhost:8081/api'
  };
  
  if (env == 'dev') {
    // configuración para desarrollo local
    karate.log('Running tests in DEV environment');
  } else if (env == 'docker') {
    // configuración para ambiente Docker
    config.clientAppUrl = 'http://clientapp:8080/api';
    config.transactionAppUrl = 'http://transactionapp:8081/api';
    karate.log('Running tests in DOCKER environment');
  }
  
  // Configuración de timeouts
  karate.configure('connectTimeout', 10000);
  karate.configure('readTimeout', 10000);
  
  return config;
}
