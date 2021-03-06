package org.codedivoire.dembesi.usermanagement.service;

import org.codedivoire.dembesi.usermanagement.entity.Group;
import org.codedivoire.dembesi.usermanagement.entity.Profile;
import org.codedivoire.dembesi.usermanagement.entity.Role;
import org.codedivoire.dembesi.usermanagement.entity.User;
import org.codedivoire.dembesi.common.model.TemporalEventData;
import org.codedivoire.dembesi.usermanagement.repository.GroupRepository;
import org.codedivoire.dembesi.usermanagement.repository.ProfileRepository;
import org.codedivoire.dembesi.usermanagement.repository.RoleRepository;
import org.codedivoire.dembesi.usermanagement.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

/**
 * @author Christian Amani on 17/01/2019.
 */
@Service
public class UserService {

    private final Logger LOG = LoggerFactory.getLogger(UserService.class);

    private final GroupRepository groupRepository;
    private final RoleRepository roleRepository;
    private final ProfileRepository profileRepository;
    private final UserRepository userRepository;

    @Autowired
    public UserService(GroupRepository groupRepository, RoleRepository roleRepository
            , ProfileRepository profileRepository, UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.roleRepository = roleRepository;
        this.profileRepository = profileRepository;
        this.userRepository = userRepository;
    }

    public Profile addRole(long profileId, String roleName) {
        LOG.debug("Debut du Process 'addRole'");
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        Profile profile = null;
        if (optionalProfile.isPresent()) {
            profile = optionalProfile.get();
            User user = profile.getUser();
            Optional<Role> optionalRole = roleRepository.findByNameIgnoreCase(roleName);
            optionalRole.ifPresent(user::addRole);
            profile.addUser(user);
            profileRepository.save(profile);
        }
        return profile;
    }

    public void removeRoles(long profileId) {
        LOG.debug("Debut du Process 'removeRoles'");
        Optional<Profile> optionalProfile = profileRepository.findById(profileId);
        if (optionalProfile.isPresent()) {
            Profile profile = optionalProfile.get();
            User user = profile.getUser();
            user.setRoles(new HashSet<>());
            userRepository.save(user);
        }
    }

    public void setGroupFromProfile(long id, String groupName) {
        LOG.debug("Debut du Process 'setGroupFromProfile'");
        Optional<Profile> optionalProfile = profileRepository.findById(id);
        optionalProfile.ifPresent(profile -> {
            User user = profile.getUser();
            Optional<Group> optionalGroup = groupRepository.findByName(groupName);
            optionalGroup.ifPresent(user::setGroup);
            profile.addUser(user);
            profileRepository.save(profile);
        });
    }

    public void setGroupFromUser(long id,String groupName) {
        LOG.debug("Debut du Process 'setGroupFromUser'");
        Optional<User> optionalUser = userRepository.findById(id);
        optionalUser.ifPresent(profile -> {
            User user = optionalUser.get();
            Optional<Group> optionalGroup = groupRepository.findByName(groupName);
            optionalGroup.ifPresent(user::setGroup);
            userRepository.save(user);
        });
    }

    public void deleted(long userId) {
        LOG.debug("Debut du Process 'deleted'");
        Optional<User> optionalUser = userRepository.findById(userId);
        optionalUser.ifPresent(user -> {
            TemporalEventData temporalEventDataUser = user.getTemporalEventData();
            temporalEventDataUser.setDeleted(LocalDateTime.now(Clock.systemUTC()));
            user.setTemporalEventData(temporalEventDataUser);
            Profile profile = user.getProfile();
            TemporalEventData temporalEventDataProfile = user.getTemporalEventData();
            temporalEventDataProfile.setDeleted(LocalDateTime.now(Clock.systemUTC()));
            profile.setTemporalEventData(temporalEventDataProfile);
            userRepository.save(user);
            profileRepository.save(profile);
        });
    }

    public boolean isDeleted(long userId) {
        LOG.debug("Debut du Process 'isDeleted'");
        Optional<User> optionalUser = userRepository.findById(userId);
        return optionalUser.map(user -> user.getTemporalEventData().getDeleted() != null).orElse(true);
    }
}
