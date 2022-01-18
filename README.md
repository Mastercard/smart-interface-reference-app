# 3DS Smart Interface Reference Application
- [Overview <a name="overview"></a>](#overview-a-nameoverviewa)
  * [Compatibility <a name="compatibility"></a>](#compatibility-a-namecompatibilitya)
- [Usage <a name="usage"></a>](#usage-a-nameusagea)
  * [Prerequisites <a name="prerequisites"></a>](#prerequisites-a-nameprerequisitesa)
  * [Configuration <a name="configuration"></a>](#configuration-a-nameconfigurationa)
  * [Integrating with OpenAPI Generator <a name="integrating-with-openapi-generator"></a>](#integrating-with-openapi-generator-a-nameintegrating-with-openapi-generatora)
    + [OpenAPI Generator Plugin Configuration](#openapi-generator-plugin-configuration)
    + [Generating The API Client Sources](#generating-the-api-client-sources)
  * [Build and Execute <a name="build-and-execute"></a>](#build-and-execute-a-namebuild-and-executea)
- [Use Cases <a name="use-cases"></a>](#use-cases-a-nameuse-casesa)
  * [Frictionless Authentication](#frictionless-authentication)
  * [Challenge Authentication](#challenge-authentication)
- [PAN Encryption](#pan-encryption)
- [API Reference <a name="api-reference"></a>](#api-reference-a-nameapi-referencea)
- [Support <a name="support"></a>](#support-a-namesupporta)
- [License <a name="license"></a>](#license-a-namelicensea)
<a name="overview-a-nameoverviewa"></a>
## Overview <a name="overview"></a>
[3DS Smart Interface](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/) provides a suite of APIs to the 3DS Requestors to facilitate 3D Secure transactions. The list of available APIs for the Merchants/Requestors to integrate with are as below:

- AuthenticationApi

- SupportedVersionsApi

This reference application helps to demo to the Merchants/Requestor on how they can integrate their backend service to connect to the 3DS Smart Interface APIs to enable 3DS transactions under different use cases.

<a name="compatibility-a-namecompatibilitya"></a>
### Compatibility <a name="compatibility"></a>
* [Java 11](http://www.oracle.com/technetwork/java/javase/downloads/index.html) or later

<a name="usage-a-nameusagea"></a>
## Usage <a name="usage"></a>
<a name="prerequisites-a-nameprerequisitesa"></a>
### Prerequisites <a name="prerequisites"></a>
* [Mastercard Developers Account](https://developer.mastercard.com/dashboard) with access to Mastercard 3DS Smart Interface API
* A text editor or IDE
* [Spring Boot 2.2+](https://spring.io/projects/spring-boot)
* [Apache Maven 3.3+](https://maven.apache.org/download.cgi)
* Set up the `JAVA_HOME` environment variable to match the location of your Java installation.

<a name="configuration-a-nameconfigurationa"></a>
### Configuration <a name="configuration"></a>
* Create an account at [Mastercard Developers](https://developer.mastercard.com/account/sign-up).
* Request access to [3DS Smart Interface](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/)
* Generate a key by following the procedure for [Getting Access](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/sandbox/#getting-access) 
* Copy your .jks or .p12 keystore into `${project.basedir}/src/main/resources/security`
* Copy your .crt encryption certificate into `${project.basedir}/src/main/resources/encryption`
* Open `${project.basedir}/src/main/resources/application.properties` and configure below parameters.
* Update the following keys in `application.yml` file.
    - `si.auth.keystore`: Path where you saved your keystore. Ex: `/security/your-keystore.p12`
    - `si.auth.keystore-password`: The password to your keystore
    - `app.encryption.enabled`: Set to true if you want to encrypt the `acctNumber` field and want to run the related scenario, false otherwise
    - `app.encryption.cert`: Path where you saved the cert used for PAN Encryption. Ex: `/encryption/your-encryption-cert.crt`

* Example:

```
si:git push 
  base-path: 
  auth:
    keyStore: /security/your-keystore.p12
    keyStore-password: yourPassword 	

app:
  encryption:
    cert: /encryption/your-encryption-cert.crt
    enabled: true
```

<a name="integrating-with-openapi-generator-a-nameintegrating-with-openapi-generatora"></a>
### Integrating with OpenAPI Generator <a name="integrating-with-openapi-generator"></a>
[OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator) generates API client libraries from [OpenAPI Specs](https://github.com/OAI/OpenAPI-Specification).
OpenAPI generator is used to create the API client library (in target folder by default) and is used as dependency by the application for making requests to the MC service.

See also:
* [Generating and Configuring a Mastercard API Client](https://developer.mastercard.com/platform/documentation/security-and-authentication/generating-and-configuring-a-mastercard-api-client/)
* [OpenAPI Generator (maven Plugin)](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-maven-plugin)
* [OpenAPI Generator (executable)](https://mvnrepository.com/artifact/org.openapitools/openapi-generator-cli)
* [CONFIG OPTIONS for java](https://github.com/OpenAPITools/openapi-generator/blob/master/docs/generators/java.md)

<a name="openapi-generator-plugin-configuration"></a>
#### OpenAPI Generator Plugin Configuration
```xml
<!-- https://mvnrepository.com/artifact/org.openapitools/openapi-generator-maven-plugin -->
<plugin>
    <groupId>org.openapitools</groupId>
    <artifactId>openapi-generator-maven-plugin</artifactId>
    <version>${openapitools.version}</version>
    <executions>
        <execution>
            <goals>
                <goal>generate</goal>
            </goals>
            <configuration>
                <inputSpec>${project.basedir}/src/main/resources/reference-service.yaml</inputSpec>
                <generatorName>java</generatorName>
                <apiPackage>com.mastercard.api</apiPackage>
                <modelPackage>com.mastercard.api.model</modelPackage>
                <generateApiTests>false</generateApiTests>
                <generateModelTests>false</generateModelTests>
                <generateModelDocumentation>false</generateModelDocumentation>
                <generateApiDocumentation>false</generateApiDocumentation>
                <skipValidateSpec>false</skipValidateSpec>
                <typeMappings>
                    <typeMapping>OffsetDateTime=String</typeMapping>
                    <typeMapping>LocalDate=String</typeMapping>
                </typeMappings>
                <importMappings>
                    <importMapping>java.time.OffsetDateTime=java.lang.String</importMapping>
                    <importMapping>java.time.LocalDate=java.lang.String</importMapping>
                </importMappings>

                <configOptions>
                    <sourceFolder>src/gen/java/main</sourceFolder>
                    <supportingFiles>false</supportingFiles>
                    <dateLibrary>java8</dateLibrary>
                    <verbose>true</verbose>
                </configOptions>
            </configuration>
        </execution>
    </executions>
</plugin>
```
For more information on how this client generator works please consult the official [Github repository](https://github.com/OpenAPITools/openapi-generator)

<a name="generating-the-api-client-sources"></a>
#### Generating the API Client Sources
Now that you have all the dependencies you need, you can generate the sources. To do this, use one of the following two methods:

`Using IDE`
* **Method 1**<br/>
  In IntelliJ IDEA, open the Maven window **(View > Tool Windows > Maven)**. Click the icons `Reimport All Maven Projects` and `Generate Sources and Update Folders for All Projects`

* **Method 2**<br/>
  In the same menu, navigate to the commands **({Project name} > Lifecycle)**, select `clean` and `compile` then click the icon `Run Maven Build`.

`Using Terminal`
* Navigate to the root directory of the project within a terminal window and execute `mvn clean compile` command.

<a name="build-and-execute-a-namebuild-and-executea"></a>
### Build and Execute <a name="build-and-execute"></a>
Once you’ve added the correct properties, we can build the application. We can do this by navigating to the project’s base directory from the terminal and running the following command

`mvn clean install`

When the project builds successfully, you can run the following command to start the project

`java -jar target/smart-interface-reference-1.0.0.jar`

<a name="use-cases-a-nameuse-casesa"></a>
## Use Cases <a name="use-cases"></a>

<a name="frictionless-authentication"></a>
### Frictionless Authentication
Frictionless authentication occurs when a submitted request does not require an Additional Factor of Authentication (AFA) as deemed by either an Issuer ACS, a Directory Server (DS) Authentication service, or by a country or regional regulatory requirement.

Frictionless authentication involves the SupportedVersions and Authentication APIs.

[Frictionless Authentication - Browser](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/usecases/frictionless_authentication_initiated_from_a_browser/) <a name="frictionless-browser"></a>

[Frictionless Authentication - Mobile App](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/usecases/frictionless_authentication_initiated_from_a_mobile_application_androidios/) <a name="frictionless-app"></a>

<a name="challenge-authentication"></a>
### Challenge Authentication

If the ACS determines that any further cardholder interaction is required to complete the authentication, the Frictionless Flow transitions into the Challenge Flow. The requestor decides whether to proceed with the challenge or terminate the 3-D Secure authentication process.

Challenge authentication involves  the SupportedVersions and Authentication APIs.

[Challenge Authentication - Browser](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/usecases/challenge_authentication_initiated_from_a_browser/) <a name="challenge-browser"></a>

[Challenge Authentication - Mobile App](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/usecases/challenge_authentication_initiated_from_a_mobile_application_androidios/) <a name="challenge-app"></a>


Decoupled Authentication is a method whereby the authentication can occur independently from the cardholder’s experience with the 3DS Requestor.

This type of authentication usually involves special consideration from other challenge flows for the timing of results.

[Decoupled Authentication](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/usecases/decoupled_authentication/) <a name="decoupled"></a>


<a name="pan-encryption"></a>
## PAN Encryption

To submit an encrypted PAN, the requestor must:

Support RSA-OAEP-256 based algorithm for JWE encryption.
Pair with any of the below AES algorithms for symmetric encryption:
* A128CBC-HS256
* A128GCM

Ensure that the generated JWE protected header of the encrypted payload includes the key identifier (KID) of the public key that is being used to perform the encryption. We recommend using the SHA1 Thumbprint of the downloaded public key certificate to be used in the “kid” field.

A sample JWE Protected Header element might look like this.

```json
{
  "alg": "RSA-OAEP-256",
  "enc": "A128GCM",
  "kid": "8DB2A39BF250AC2761FF4C2F2FAACB7B3DC93416"
}
```

Add encrypted PAN to the Authentications or Supported Versions API – Data Element encryptedData.


<a name="api-reference-a-nameapi-referencea"></a>
## API Reference <a name="api-reference"></a>

The API Reference can be found [here](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/api-reference/)

<a name="support-a-namesupporta"></a>
## Support <a name="support"></a>
For more information, please refer to the [Support Page](https://developer.mastercard.com/3-d-secure-merchant-interface/documentation/support/)

<a name="license-a-namelicensea"></a>
## License <a name="license"></a>
Copyright 2021 Mastercard

Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
the License. You may obtain a copy of the License at:

       http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
specific language governing permissions and limitations under the License.


