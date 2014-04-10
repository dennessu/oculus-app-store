

package com.junbo.identity.data.mapper;

import com.junbo.oom.core.filter.ElementMappingEvent;
import com.junbo.oom.core.filter.ElementMappingFilter;
import com.junbo.oom.core.filter.PropertyMappingEvent;
import com.junbo.oom.core.filter.PropertyMappingFilter;

import com.junbo.oom.core.builtin.BuiltinMapper;
import com.junbo.identity.data.mapper.CommonMapper;
import com.junbo.identity.spec.model.password.PasswordRule;
import com.junbo.oom.core.MappingContext;
import com.junbo.identity.data.entity.password.PasswordRuleEntity;
import java.util.List;
import java.util.ArrayList;
import java.lang.String;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.spec.model.password.PasswordRuleDetail;
import java.lang.Short;
import java.util.Date;
import java.lang.Integer;
import com.junbo.common.id.PasswordRuleId;
import java.lang.Long;
import com.junbo.identity.spec.model.user.UserTosAcceptance;
import com.junbo.identity.data.entity.user.UserTosAcceptanceEntity;
import com.junbo.common.id.UserTosAcceptanceId;
import com.junbo.common.id.UserId;
import com.junbo.identity.spec.model.user.UserOptIn;
import com.junbo.identity.data.entity.user.UserOptInEntity;
import com.junbo.common.id.UserOptInId;
import com.junbo.identity.spec.model.user.UserFederation;
import com.junbo.identity.data.entity.user.UserFederationEntity;
import com.junbo.common.id.UserFederationId;
import com.junbo.identity.spec.model.user.UserDeviceProfile;
import com.junbo.identity.data.entity.user.UserDeviceProfileEntity;
import com.junbo.common.id.UserDeviceProfileId;
import com.junbo.common.id.DeviceId;
import com.junbo.identity.data.entity.app.AppEntity;
import com.junbo.identity.spec.model.app.App;
import com.junbo.common.id.AppId;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.data.entity.app.AppSecretEntity;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.spec.model.app.AppSecret;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.data.entity.app.AppGroupEntity;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.spec.model.app.AppGroup;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.data.entity.user.UserEntity;
import java.util.List;
import java.util.ArrayList;
import com.junbo.identity.spec.model.user.User;
import com.junbo.identity.data.entity.user.UserProfileEntity;
import com.junbo.identity.spec.model.user.UserProfile;
import com.junbo.common.id.UserProfileId;

@org.springframework.stereotype.Component("com.junbo.identity.data.mapper.ModelMapperImpl")
public class ModelMapperImpl implements ModelMapper {


    @org.springframework.beans.factory.annotation.Autowired
    public BuiltinMapper __builtinMapper;

    @org.springframework.beans.factory.annotation.Autowired
    public CommonMapper __commonMapper;


    
    public PasswordRuleEntity toPasswordRule(PasswordRule passwordRule, MappingContext context) {
    
        if (passwordRule == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PasswordRuleEntity __result = new PasswordRuleEntity();
    
        {
        
            List<String> __sourceProperty = passwordRule.getAllowedCharacterSet();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAllowedCharacterSet(__commonMapper.explicitMethod_listStringToJson(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("allowedCharacterSet");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("allowedCharacterSet");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAllowedCharacterSet(__commonMapper.explicitMethod_listStringToJson(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<String> __sourceProperty = passwordRule.getNotAllowedCharacterSet();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setNotAllowedCharacterSet(__commonMapper.explicitMethod_listStringToJson(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("notAllowedCharacterSet");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("notAllowedCharacterSet");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setNotAllowedCharacterSet(__commonMapper.explicitMethod_listStringToJson(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<PasswordRuleDetail> __sourceProperty = passwordRule.getPasswordRuleDetails();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPasswordRuleDetails(__commonMapper.listPasswordRuleDetailsToJson(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("passwordRuleDetails");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("passwordRuleDetails");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPasswordRuleDetails(__commonMapper.listPasswordRuleDetailsToJson(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = passwordRule.getPasswordStrength();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("passwordStrength");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("passwordStrength");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = passwordRule.getPasswordStrength();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("passwordStrength");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("passwordStrength");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = passwordRule.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = passwordRule.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = passwordRule.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            PasswordRuleId __sourceProperty = passwordRule.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toPasswordRuleId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRule.class);
                __event.setSourcePropertyType(PasswordRuleId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRuleEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toPasswordRuleId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public PasswordRule toPasswordRule(PasswordRuleEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        PasswordRule __result = new PasswordRule();
    
        {
        
            String __sourceProperty = entity.getAllowedCharacterSet();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAllowedCharacterSet(__commonMapper.explicitMethod_jsonToListString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("allowedCharacterSet");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("allowedCharacterSet");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAllowedCharacterSet(__commonMapper.explicitMethod_jsonToListString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getNotAllowedCharacterSet();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setNotAllowedCharacterSet(__commonMapper.explicitMethod_jsonToListString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("notAllowedCharacterSet");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("notAllowedCharacterSet");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setNotAllowedCharacterSet(__commonMapper.explicitMethod_jsonToListString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getPasswordRuleDetails();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPasswordRuleDetails(__commonMapper.jsonToListPasswordRuleDetails(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("passwordRuleDetails");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("passwordRuleDetails");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPasswordRuleDetails(__commonMapper.jsonToListPasswordRuleDetails(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getPasswordStrength();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("passwordStrength");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("passwordStrength");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = entity.getPasswordStrength();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("passwordStrength");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("passwordStrength");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPasswordStrength(__commonMapper.explicitMethod_toUserPasswordStrength(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toPasswordRuleId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(PasswordRuleEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(PasswordRule.class);
                __event.setTargetPropertyType(PasswordRuleId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toPasswordRuleId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserTosAcceptanceEntity toUserTosAcceptance(UserTosAcceptance entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserTosAcceptanceEntity __result = new UserTosAcceptanceEntity();
    
        {
        
            String __sourceProperty = entity.getTos();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTosAcceptanceUrl(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("tos");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("tosAcceptanceUrl");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTosAcceptanceUrl(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getTos();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTosAcceptanceUrl(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("tos");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("tosAcceptanceUrl");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTosAcceptanceUrl(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserTosAcceptanceId __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserTosAcceptanceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(UserTosAcceptanceId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserTosAcceptanceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getDateAccepted();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDateAccepted(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptance.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("dateAccepted");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptanceEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("dateAccepted");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDateAccepted(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserTosAcceptance toUserTosAcceptance(UserTosAcceptanceEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserTosAcceptance __result = new UserTosAcceptance();
    
        {
        
            String __sourceProperty = entity.getTosAcceptanceUrl();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTos(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("tosAcceptanceUrl");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("tos");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTos(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getTosAcceptanceUrl();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setTos(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("tosAcceptanceUrl");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("tos");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setTos(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserTosAcceptanceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(UserTosAcceptanceId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserTosAcceptanceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getDateAccepted();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDateAccepted(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserTosAcceptanceEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("dateAccepted");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserTosAcceptance.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("dateAccepted");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDateAccepted(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserOptInEntity toUserOptIn(UserOptIn entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserOptInEntity __result = new UserOptInEntity();
    
        {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptIn.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptInEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptIn.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptInEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptIn.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptInEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserOptInId __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserOptInId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptIn.class);
                __event.setSourcePropertyType(UserOptInId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptInEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserOptInId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptIn.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptInEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptIn.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptInEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserOptIn toUserOptIn(UserOptInEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserOptIn __result = new UserOptIn();
    
        {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptInEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptIn.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptInEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptIn.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptInEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptIn.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserOptInId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptInEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptIn.class);
                __event.setTargetPropertyType(UserOptInId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserOptInId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptInEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptIn.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserOptInEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserOptIn.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserFederationEntity toUserFederation(UserFederation userFederation, MappingContext context) {
    
        if (userFederation == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserFederationEntity __result = new UserFederationEntity();
    
        {
        
            Date __sourceProperty = userFederation.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = userFederation.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = userFederation.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserFederationId __sourceProperty = userFederation.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserFederationId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(UserFederationId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserFederationId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = userFederation.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userFederation.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userFederation.getValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederation.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("value");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederationEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("value");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserFederation toUserFederation(UserFederationEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserFederation __result = new UserFederation();
    
        {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserFederationId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(UserFederationId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserFederationId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserFederationEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("value");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserFederation.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("value");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserDeviceProfileEntity toUserDeviceProfile(UserDeviceProfile entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserDeviceProfileEntity __result = new UserDeviceProfileEntity();
    
        {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserDeviceProfileId __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserDeviceProfileId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(UserDeviceProfileId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserDeviceProfileId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            DeviceId __sourceProperty = entity.getDeviceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDeviceId(__commonMapper.toDeviceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfile.class);
                __event.setSourcePropertyType(DeviceId.class);
                __event.setSourcePropertyName("deviceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfileEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("deviceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDeviceId(__commonMapper.toDeviceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserDeviceProfile toUserDeviceProfile(UserDeviceProfileEntity entity, MappingContext context) {
    
        if (entity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserDeviceProfile __result = new UserDeviceProfile();
    
        {
        
            Date __sourceProperty = entity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = entity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = entity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserDeviceProfileId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(UserDeviceProfileId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserDeviceProfileId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = entity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = entity.getDeviceId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDeviceId(__commonMapper.toDeviceId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserDeviceProfileEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("deviceId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserDeviceProfile.class);
                __event.setTargetPropertyType(DeviceId.class);
                __event.setTargetPropertyName("deviceId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDeviceId(__commonMapper.toDeviceId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public App toApp(AppEntity appEntity, MappingContext context) {
    
        if (appEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        App __result = new App();
    
        {
        
            Long __sourceProperty = appEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toAppId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(AppId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toAppId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = appEntity.getOwnerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOwnerId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("ownerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("ownerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOwnerId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("name");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<AppSecretEntity> __sourceProperty = appEntity.getAppSecrets();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAppSecrets(__fromListAppSecretEntityToListAppSecret(__sourceProperty, context));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("appSecrets");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("appSecrets");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAppSecrets(__fromListAppSecretEntityToListAppSecret(__sourceProperty, context));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<AppGroupEntity> __sourceProperty = appEntity.getGroups();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setGroups(__fromListAppGroupEntityToListAppGroup(__sourceProperty, context));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("groups");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("groups");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setGroups(__fromListAppGroupEntityToListAppGroup(__sourceProperty, context));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getRedirectUris();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRedirectUris(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("redirectUris");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("redirectUris");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRedirectUris(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getDefaultRedirectUri();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDefaultRedirectUri(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("defaultRedirectUri");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("defaultRedirectUri");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDefaultRedirectUri(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getLogoutUris();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLogoutUris(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("logoutUris");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("logoutUris");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLogoutUris(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getResponseTypes();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResponseTypes(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("responseTypes");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("responseTypes");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResponseTypes(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getGrantTypes();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setGrantTypes(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("grantTypes");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("grantTypes");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setGrantTypes(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getIpWhitelist();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIpWhitelist(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("ipWhitelist");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("ipWhitelist");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIpWhitelist(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = appEntity.getProperties();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("properties");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(App.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("properties");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public List<AppGroup> __fromListAppGroupEntityToListAppGroup(List<AppGroupEntity> source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<AppGroup> __result = new ArrayList<AppGroup>();
    
        for (AppGroupEntity __sourceItem : source) {
    
    
            ElementMappingFilter __filter = context.getElementMappingFilter();
            if (__filter == null) {
    
                AppGroup __targetItem =
                    __fromAppGroupEntityToAppGroup(__sourceItem, context);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(AppGroupEntity.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(AppGroup.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, context);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, context);
    
                    AppGroup __targetItem =
                        __fromAppGroupEntityToAppGroup(__sourceItem, context);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, context);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public AppGroup __fromAppGroupEntityToAppGroup(AppGroupEntity source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        AppGroup __result = new AppGroup();
    
        {
        
            String __sourceProperty = source.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppGroupEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppGroup.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("name");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getPermissions();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPermissions(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppGroupEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("permissions");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppGroup.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("permissions");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPermissions(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<UserEntity> __sourceProperty = source.getMembers();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMembers(__fromListUserEntityToListUser(__sourceProperty, context));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppGroupEntity.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("members");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppGroup.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("members");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMembers(__fromListUserEntityToListUser(__sourceProperty, context));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public List<User> __fromListUserEntityToListUser(List<UserEntity> source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<User> __result = new ArrayList<User>();
    
        for (UserEntity __sourceItem : source) {
    
    
            ElementMappingFilter __filter = context.getElementMappingFilter();
            if (__filter == null) {
    
                User __targetItem =
                    toUser(__sourceItem, context);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(UserEntity.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(User.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, context);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, context);
    
                    User __targetItem =
                        toUser(__sourceItem, context);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, context);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public List<AppSecret> __fromListAppSecretEntityToListAppSecret(List<AppSecretEntity> source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<AppSecret> __result = new ArrayList<AppSecret>();
    
        for (AppSecretEntity __sourceItem : source) {
    
    
            ElementMappingFilter __filter = context.getElementMappingFilter();
            if (__filter == null) {
    
                AppSecret __targetItem =
                    __fromAppSecretEntityToAppSecret(__sourceItem, context);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(AppSecretEntity.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(AppSecret.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, context);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, context);
    
                    AppSecret __targetItem =
                        __fromAppSecretEntityToAppSecret(__sourceItem, context);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, context);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public AppSecret __fromAppSecretEntityToAppSecret(AppSecretEntity source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        AppSecret __result = new AppSecret();
    
        {
        
            String __sourceProperty = source.getValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppSecretEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("value");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppSecret.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("value");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = source.getExpiredBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExpiredBy(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppSecretEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("expiredBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppSecret.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("expiredBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExpiredBy(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppSecretEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppSecret.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public AppEntity toAppEntity(App app, MappingContext context) {
    
        if (app == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        AppEntity __result = new AppEntity();
    
        {
        
            AppId __sourceProperty = app.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toAppId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(AppId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toAppId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = app.getOwnerId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setOwnerId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("ownerId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("ownerId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setOwnerId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("name");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getRedirectUris();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRedirectUris(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("redirectUris");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("redirectUris");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRedirectUris(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getDefaultRedirectUri();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDefaultRedirectUri(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("defaultRedirectUri");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("defaultRedirectUri");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDefaultRedirectUri(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getLogoutUris();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLogoutUris(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("logoutUris");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("logoutUris");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLogoutUris(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getResponseTypes();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResponseTypes(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("responseTypes");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("responseTypes");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResponseTypes(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getGrantTypes();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setGrantTypes(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("grantTypes");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("grantTypes");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setGrantTypes(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getIpWhitelist();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setIpWhitelist(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("ipWhitelist");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("ipWhitelist");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setIpWhitelist(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = app.getProperties();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("properties");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("properties");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setProperties(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<AppSecret> __sourceProperty = app.getAppSecrets();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setAppSecrets(__fromListAppSecretToListAppSecretEntity(__sourceProperty, context));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("appSecrets");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("appSecrets");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setAppSecrets(__fromListAppSecretToListAppSecretEntity(__sourceProperty, context));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<AppGroup> __sourceProperty = app.getGroups();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setGroups(__fromListAppGroupToListAppGroupEntity(__sourceProperty, context));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(App.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("groups");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppEntity.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("groups");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setGroups(__fromListAppGroupToListAppGroupEntity(__sourceProperty, context));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public List<AppGroupEntity> __fromListAppGroupToListAppGroupEntity(List<AppGroup> source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<AppGroupEntity> __result = new ArrayList<AppGroupEntity>();
    
        for (AppGroup __sourceItem : source) {
    
    
            ElementMappingFilter __filter = context.getElementMappingFilter();
            if (__filter == null) {
    
                AppGroupEntity __targetItem =
                    __fromAppGroupToAppGroupEntity(__sourceItem, context);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(AppGroup.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(AppGroupEntity.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, context);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, context);
    
                    AppGroupEntity __targetItem =
                        __fromAppGroupToAppGroupEntity(__sourceItem, context);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, context);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public AppGroupEntity __fromAppGroupToAppGroupEntity(AppGroup source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        AppGroupEntity __result = new AppGroupEntity();
    
        {
        
            String __sourceProperty = source.getName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppGroup.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("name");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppGroupEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("name");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getPermissions();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPermissions(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppGroup.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("permissions");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppGroupEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("permissions");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPermissions(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            List<User> __sourceProperty = source.getMembers();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMembers(__fromListUserToListUserEntity(__sourceProperty, context));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppGroup.class);
                __event.setSourcePropertyType(List.class);
                __event.setSourcePropertyName("members");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppGroupEntity.class);
                __event.setTargetPropertyType(List.class);
                __event.setTargetPropertyName("members");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMembers(__fromListUserToListUserEntity(__sourceProperty, context));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public List<UserEntity> __fromListUserToListUserEntity(List<User> source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<UserEntity> __result = new ArrayList<UserEntity>();
    
        for (User __sourceItem : source) {
    
    
            ElementMappingFilter __filter = context.getElementMappingFilter();
            if (__filter == null) {
    
                UserEntity __targetItem =
                    toUserEntity(__sourceItem, context);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(User.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(UserEntity.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, context);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, context);
    
                    UserEntity __targetItem =
                        toUserEntity(__sourceItem, context);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, context);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public List<AppSecretEntity> __fromListAppSecretToListAppSecretEntity(List<AppSecret> source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
    
        List<AppSecretEntity> __result = new ArrayList<AppSecretEntity>();
    
        for (AppSecret __sourceItem : source) {
    
    
            ElementMappingFilter __filter = context.getElementMappingFilter();
            if (__filter == null) {
    
                AppSecretEntity __targetItem =
                    __fromAppSecretToAppSecretEntity(__sourceItem, context);
    
                __result.add(__targetItem);
    
            } else {
                ElementMappingEvent __event = new ElementMappingEvent();
    
                __event.setSourceElementType(AppSecret.class);
                __event.setSourceElement(__sourceItem);
                __event.setTargetElementType(AppSecretEntity.class);
    
                boolean __skipped = __filter.skipElementMapping(__event, context);
    
                if (!__skipped) {
                    __filter.beginElementMapping(__event, context);
    
                    AppSecretEntity __targetItem =
                        __fromAppSecretToAppSecretEntity(__sourceItem, context);
    
                    __result.add(__targetItem);
    
                    __filter.endElementMapping(__event, context);
                }
            }
    
        }
    
        return __result;
    }
    

    
    public AppSecretEntity __fromAppSecretToAppSecretEntity(AppSecret source, MappingContext context) {
    
        if (source == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        AppSecretEntity __result = new AppSecretEntity();
    
        {
        
            String __sourceProperty = source.getValue();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppSecret.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("value");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppSecretEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("value");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setValue(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = source.getExpiredBy();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setExpiredBy(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppSecret.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("expiredBy");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppSecretEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("expiredBy");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setExpiredBy(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = source.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(AppSecret.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(AppSecretEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserProfile toUserProfile(UserProfileEntity userProfileEntity, MappingContext context) {
    
        if (userProfileEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserProfile __result = new UserProfile();
    
        {
        
            Date __sourceProperty = userProfileEntity.getDob();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDateOfBirth(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("dob");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("dateOfBirth");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDateOfBirth(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = userProfileEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = userProfileEntity.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = userProfileEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = userProfileEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = userProfileEntity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = userProfileEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserProfileId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(UserProfileId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserProfileId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = userProfileEntity.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfileEntity.getRegion();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRegion(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("region");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("region");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRegion(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfileEntity.getFirstName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("firstName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("firstName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfileEntity.getMiddleName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("middleName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("middleName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfileEntity.getLastName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("lastName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("lastName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfileEntity.getLocale();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLocale(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfileEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("locale");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfile.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("locale");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLocale(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserProfileEntity toUserProfileEntity(UserProfile userProfile, MappingContext context) {
    
        if (userProfile == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserProfileEntity __result = new UserProfileEntity();
    
        {
        
            Date __sourceProperty = userProfile.getDateOfBirth();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setDob(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("dateOfBirth");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("dob");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setDob(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getType();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("type");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("type");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setType(__commonMapper.explicitMethod_toUserProfileType(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = userProfile.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserProfileId __sourceProperty = userProfile.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserProfileId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(UserProfileId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserProfileId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = userProfile.getUserId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("userId");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("userId");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getRegion();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setRegion(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("region");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("region");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setRegion(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getFirstName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("firstName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("firstName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setFirstName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getMiddleName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("middleName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("middleName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setMiddleName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getLastName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("lastName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("lastName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLastName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userProfile.getLocale();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setLocale(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserProfile.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("locale");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserProfileEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("locale");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setLocale(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public User toUser(UserEntity userEntity, MappingContext context) {
    
        if (userEntity == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        User __result = new User();
    
        {
        
            Short __sourceProperty = userEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Short __sourceProperty = userEntity.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(Short.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = userEntity.getCreatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("createdTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("createdTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setCreatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Date __sourceProperty = userEntity.getUpdatedTime();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(Date.class);
                __event.setSourcePropertyName("updatedTime");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(Date.class);
                __event.setTargetPropertyName("updatedTime");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUpdatedTime(__builtinMapper.fromDateToDate(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = userEntity.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Long __sourceProperty = userEntity.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(Long.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(UserId.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = userEntity.getUserName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(UserEntity.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("userName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(User.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("userName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }

    
    public UserEntity toUserEntity(User user, MappingContext context) {
    
        if (user == null) {
            return null;
        }
    
        if (context.getSkipMapping() == Boolean.TRUE) {
            return null;
        }
    
        UserEntity __result = new UserEntity();
    
        {
        
            String __sourceProperty = user.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(User.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = user.getStatus();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(User.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("status");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserEntity.class);
                __event.setTargetPropertyType(Short.class);
                __event.setTargetPropertyName("status");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setStatus(__commonMapper.explicitMethod_toUserStatus(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            Integer __sourceProperty = user.getResourceAge();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(User.class);
                __event.setSourcePropertyType(Integer.class);
                __event.setSourcePropertyName("resourceAge");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserEntity.class);
                __event.setTargetPropertyType(Integer.class);
                __event.setTargetPropertyName("resourceAge");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setResourceAge(__commonMapper.toInteger(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            UserId __sourceProperty = user.getId();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setId(__commonMapper.toUserId(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(User.class);
                __event.setSourcePropertyType(UserId.class);
                __event.setSourcePropertyName("id");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserEntity.class);
                __event.setTargetPropertyType(Long.class);
                __event.setTargetPropertyName("id");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setId(__commonMapper.toUserId(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = user.getUserName();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setUserName(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(User.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("userName");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("userName");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setUserName(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
            {
        
            String __sourceProperty = user.getPassword();
        
            PropertyMappingFilter __filter = context.getPropertyMappingFilter();
            if (__filter == null) {
                __result.setPassword(__builtinMapper.fromStringToString(__sourceProperty));
            } else {
                PropertyMappingEvent __event = new PropertyMappingEvent();
        
                __event.setSourceType(User.class);
                __event.setSourcePropertyType(String.class);
                __event.setSourcePropertyName("password");
                __event.setSourceProperty(__sourceProperty);
        
                __event.setTargetType(UserEntity.class);
                __event.setTargetPropertyType(String.class);
                __event.setTargetPropertyName("password");
        
                boolean __skipped = __filter.skipPropertyMapping(__event, context);
        
                if (!__skipped) {
                    __filter.beginPropertyMapping(__event, context);
        
                    __result.setPassword(__builtinMapper.fromStringToString(__sourceProperty));
        
                    __filter.endPropertyMapping(__event, context);
                }
            }
        
        }
        
        return __result;
    }
}
