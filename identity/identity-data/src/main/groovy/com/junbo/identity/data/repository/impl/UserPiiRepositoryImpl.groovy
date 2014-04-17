package com.junbo.identity.data.repository.impl

import com.junbo.common.id.AddressId
import com.junbo.common.id.UserPiiId
import com.junbo.identity.data.dao.UserAddressDAO
import com.junbo.identity.data.dao.UserEmailDAO
import com.junbo.identity.data.dao.UserNameDAO
import com.junbo.identity.data.dao.UserPhoneNumberDAO
import com.junbo.identity.data.dao.UserPiiDAO
import com.junbo.identity.data.entity.user.UserAddressEntity
import com.junbo.identity.data.entity.user.UserEmailEntity
import com.junbo.identity.data.entity.user.UserNameEntity
import com.junbo.identity.data.entity.user.UserPhoneNumberEntity
import com.junbo.identity.data.entity.user.UserPiiEntity
import com.junbo.identity.data.mapper.ModelMapper
import com.junbo.identity.data.repository.UserPiiRepository
import com.junbo.identity.spec.options.list.UserEmailListOptions
import com.junbo.identity.spec.options.list.UserPhoneNumberListOptions
import com.junbo.identity.spec.v1.model.UserEmail
import com.junbo.identity.spec.v1.model.UserPhoneNumber
import com.junbo.identity.spec.v1.model.UserPii
import com.junbo.identity.spec.v1.option.list.UserPiiListOptions
import com.junbo.langur.core.promise.Promise
import com.junbo.oom.core.MappingContext
import groovy.transform.CompileStatic
import org.apache.commons.collections.CollectionUtils
import org.springframework.beans.factory.annotation.Autowired

/**
 * Created by liangfu on 4/10/14.
 */
@CompileStatic
class UserPiiRepositoryImpl implements UserPiiRepository {

    @Autowired
    private ModelMapper modelMapper

    @Autowired
    private UserPiiDAO userPiiDAO

    @Autowired
    private UserPhoneNumberDAO userPhoneNumberDAO

    @Autowired
    private UserEmailDAO userEmailDAO

    @Autowired
    private UserAddressDAO userAddressDAO

    @Autowired
    private UserNameDAO userNameDAO

    @Override
    Promise<UserPii> create(UserPii userPii) {
        UserPiiEntity entity = modelMapper.toUserPii(userPii, new MappingContext())
        entity = userPiiDAO.save(entity)
        createPiiSubInfo(userPii)
        return get(new UserPiiId((Long)entity.id))
    }

    @Override
    Promise<UserPii> update(UserPii userPii) {
        UserPiiEntity userPiiEntity = modelMapper.toUserPii(userPii, new MappingContext())
        userPiiDAO.update(userPiiEntity)
        deletePiiSubInfo((UserPiiId)userPii.id)
        createPiiSubInfo(userPii)

        return get((UserPiiId)userPii.id)
    }

    @Override
    Promise<UserPii> get(UserPiiId userPiiId) {
        UserPiiEntity userPiiEntity = userPiiDAO.get(userPiiId.value)
        UserPii userPii = modelMapper.toUserPii(userPiiEntity, new MappingContext())
        if (userPii == null) {
            return Promise.pure(null)
        }

        UserNameEntity userNameEntity = userNameDAO.findByUserPiiId(userPiiId.value)
        userPii.name = modelMapper.toUserName(userNameEntity, new MappingContext())

        List<UserEmailEntity> userEmailEntities = userEmailDAO.search(userPiiId.value, new UserEmailListOptions(
                userPiiId: userPiiId
        ))
        if (!CollectionUtils.isEmpty(userEmailEntities)) {
            userPii.emails = new HashMap<>()
            userEmailEntities.each { UserEmailEntity userEmailEntity ->
                if (userEmailEntity != null) {
                    UserEmail userEmail = modelMapper.toUserEmail(userEmailEntity, new MappingContext())
                    userPii.emails.put(userEmail.type, userEmail)
                }
            }
        }

        List<UserPhoneNumberEntity> userPhoneNumberEntities = userPhoneNumberDAO.search(userPiiId.value,
                new UserPhoneNumberListOptions(
                        userPiiId: userPiiId
                ))
        if (!CollectionUtils.isEmpty(userPhoneNumberEntities)) {
            userPii.phoneNumbers = new HashMap<>()
            userPhoneNumberEntities.each { UserPhoneNumberEntity userPhoneNumberEntity ->
                if (userPhoneNumberEntity != null) {
                    UserPhoneNumber userPhoneNumber =
                            modelMapper.toUserPhoneNumber(userPhoneNumberEntity, new MappingContext())

                    userPii.phoneNumbers.put(userPhoneNumber.type, userPhoneNumber)
                }
            }
        }

        List<UserAddressEntity> userAddressEntities = userAddressDAO.search(userPiiId.value)
        if (!CollectionUtils.isEmpty(userAddressEntities)) {
            userPii.addressBook = new ArrayList<>()
            userAddressEntities.each { UserAddressEntity userAddressEntity ->
                if (userAddressEntity != null) {
                    userPii.addressBook.add(new AddressId(userAddressEntity.id))
                }
            }
        }

        return Promise.pure(userPii)
    }

    @Override
    Promise<Void> delete(UserPiiId userPiiId) {
        deletePiiSubInfo(userPiiId)
        userPiiDAO.delete(userPiiId.value)
        return Promise.pure(null)
    }

    private void createPiiSubInfo(UserPii userPii) {
        if (userPii.name != null) {
            UserNameEntity userNameEntity = modelMapper.toUserName(userPii.name, new MappingContext())
            userNameEntity.setUserPiiId(((UserPiiId)userPii.id).value)
            userNameDAO.create(userNameEntity)
        }

        if (userPii.emails != null) {
            userPii.emails.each { Map.Entry entry ->
                if (entry != null) {
                    String type = (String)entry.key
                    UserEmail userEmail = (UserEmail)entry.value

                    userEmail.setUserPiiId(new UserPiiId(((UserPiiId)userPii.id).value))
                    userEmail.setType(type)

                    UserEmailEntity userEmailEntity = modelMapper.toUserEmail(userEmail, new MappingContext())
                    userEmailDAO.create(userEmailEntity)
                }
            }
        }

        if (userPii.phoneNumbers != null) {
            userPii.phoneNumbers.each { Map.Entry entry ->
                if (entry != null) {
                    String type = (String)entry.key
                    UserPhoneNumber userPhoneNumber = (UserPhoneNumber)entry.value

                    userPhoneNumber.setUserPiiId((UserPiiId)userPii.id)
                    userPhoneNumber.setType(type)

                    UserPhoneNumberEntity userPhoneNumberEntity =
                            modelMapper.toUserPhoneNumber(userPhoneNumber, new MappingContext())
                    userPhoneNumberDAO.save(userPhoneNumberEntity)
                }
            }
        }

        if (userPii.addressBook != null) {
            userPii.addressBook.each { AddressId addressId ->
                UserAddressEntity userAddressEntity = new UserAddressEntity()
                userAddressEntity.addressId = addressId.value
                userAddressEntity.userPiiId = ((UserPiiId)userPii.id).value

                userAddressDAO.save(userAddressEntity)
            }
        }
    }

    private void deletePiiSubInfo(UserPiiId userPiiId) {
        UserNameEntity userNameEntity = userNameDAO.findByUserPiiId(userPiiId.value)
        if (userNameEntity != null) {
            userNameDAO.delete(userNameEntity.id)
        }

        List<UserEmailEntity> userEmailEntities = userEmailDAO.search(userPiiId.value, new UserEmailListOptions(
                userPiiId: userPiiId
        ))
        if (!CollectionUtils.isEmpty(userEmailEntities)) {
            userEmailEntities.each { UserEmailEntity userEmailEntity ->
                userEmailDAO.delete((Long)userEmailEntity.id)
            }
        }

        List<UserPhoneNumberEntity> userPhoneNumberEntities =
                userPhoneNumberDAO.search(userPiiId.value, new UserPhoneNumberListOptions(
                        userPiiId: userPiiId
                ))
        if (!CollectionUtils.isEmpty(userPhoneNumberEntities)) {
            userPhoneNumberEntities.each { UserPhoneNumberEntity userPhoneNumberEntity ->
                userPhoneNumberDAO.delete((Long)userPhoneNumberEntity.id)
            }
        }

        List<UserAddressEntity> userAddressEntities = userAddressDAO.search(userPiiId.value)
        if (!CollectionUtils.isEmpty(userAddressEntities)) {
            userAddressEntities.each { UserAddressEntity userAddressEntity ->
                userAddressDAO.delete((Long)userAddressEntity.id)
            }
        }
    }

    @Override
    Promise<List<UserPii>> search(UserPiiListOptions options) {
        List<UserPii> userPiiList = new ArrayList<>()
        if (options.userId != null) {
            UserPiiEntity userPiiEntity = userPiiDAO.findByUserId(options.userId.value)
            if (userPiiEntity != null) {
                userPiiList.add(get(new UserPiiId((Long)userPiiEntity.id)).wrapped().get())
            }
        } else if (options.email != null) {
            UserEmailEntity userEmailEntity = userEmailDAO.findByEmail(options.email)
            if (userEmailEntity != null) {
                userPiiList.add(get(new UserPiiId((Long)userEmailEntity.userPiiId)).wrapped().get())
            }
        }

        return Promise.pure(userPiiList)
    }
}
