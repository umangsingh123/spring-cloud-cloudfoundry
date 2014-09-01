/*
 * Copyright 2013-2014 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.springframework.cloud.cloudfoundry.oauth2;

import lombok.Data;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.util.StringUtils;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

/**
 * @author Dave Syer
 *
 */
@ConfigurationProperties("oauth2.resource")
@Data
public class ResourceServerProperties implements Validator {

	private String serviceId = "resource";

	@Value("${vcap.services.${oauth2.resource.serviceId:resource}.credentials.id:}")
	private String id;

	@Value("${vcap.services.${oauth2.resource.serviceId:resource}.credentials.clientId:${vcap.services.${oauth2.sso.serviceId:sso}.credentials.clientId:}}")
	private String clientId;

	@Value("${vcap.services.${oauth2.resource.serviceId:resource}.credentials.clientSecret:${vcap.services.${oauth2.sso.serviceId:sso}.credentials.clientSecret:}}")
	private String clientSecret;

	@Value("${vcap.services.${oauth2.resource.serviceId:resource}.credentials.userInfoUri:${vcap.services.${oauth2.sso.serviceId:sso}.credentials.userInfoUri:}}")
	private String userInfoUri;

	@Value("${vcap.services.${oauth2.resource.serviceId:resource}.credentials.tokenInfoUri:${vcap.services.${oauth2.sso.serviceId:sso}.credentials.tokenInfoUri:}}")
	private String tokenInfoUri;

	private boolean preferTokenInfo = true;

	public String getResourceId() {
		return !StringUtils.hasText(id) ? clientId : id;
	}

	@Override
	public boolean supports(Class<?> clazz) {
		return ResourceServerProperties.class.isAssignableFrom(clazz);
	}

	@Override
	public void validate(Object target, Errors errors) {
		ResourceServerProperties resource = (ResourceServerProperties) target;
		if (StringUtils.hasText(resource.getClientId())) {
			if (!StringUtils.hasText(resource.getClientSecret())) {
				if (!StringUtils.hasText(resource.getUserInfoUri())) {
					errors.rejectValue("userInfoUri", "missing.userInfoUri",
							"Missing userInfoUri (no client secret available)");
				}
			} else {
				if (!StringUtils.hasText(resource.getTokenInfoUri())) {
					errors.rejectValue("tokenInfoUri", "missing.tokenInfoUri",
							"Missing tokenInfoUri");
				}				
			}
		}
	}

}
