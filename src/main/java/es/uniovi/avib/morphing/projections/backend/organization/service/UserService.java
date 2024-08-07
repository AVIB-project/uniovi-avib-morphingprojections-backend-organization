package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;

import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.repository.UserRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {
	private final UserRepository userRepository;
	
	public List<User> findAll() {		
		log.debug("findAll: found all users");
		
		return (List<User>) userRepository.findAll();		
	}
	
	public User findById(String userId) {
		log.debug("findById: found user with id: {}", userId);
		
		return userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));	
	}
		
	public User save(User user) {
		log.debug("save: save user");
		
		return userRepository.save(user);
	}
	
	public void deleteById(String userId) {
		log.debug("deleteById: delete user with id: {}", userId);
		
		userRepository.deleteById(userId);
	}
	
	public User findByEmail(String email) {
		log.debug("findById: found user with id: {}", email);
		
		return userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));	
	}
}
