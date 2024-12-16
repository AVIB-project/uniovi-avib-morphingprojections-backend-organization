package es.uniovi.avib.morphing.projections.backend.organization.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import es.uniovi.avib.morphing.projections.backend.organization.domain.User;
import es.uniovi.avib.morphing.projections.backend.organization.dto.CaseUserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserInviteRequestDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserRequestDto;
import es.uniovi.avib.morphing.projections.backend.organization.dto.UserSessionDto;
import es.uniovi.avib.morphing.projections.backend.organization.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("users")
public class UserController {
	private final UserService userService;
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public ResponseEntity<List<User>> findAll() {
		List<User> users = (List<User>) userService.findAll();
					
		log.debug("findAll: found {} users", users.size());
		
		return new ResponseEntity<List<User>>(users, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{userId}")	
	public ResponseEntity<UserDto> findById(@PathVariable String userId) {
		UserDto user = userService.findById(userId);
										
		log.debug("findById: found user with userId: {}", userId);
			
		return new ResponseEntity<UserDto>(user, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{externalId}/external")	
	public ResponseEntity<UserDto> findByExternalId(@PathVariable String externalId) {
		UserDto user = userService.findByExternalId(externalId);
										
		log.debug("findByExternalId: found user with externalId: {}", externalId);
			
		return new ResponseEntity<UserDto>(user, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/organizations/{organizationId}")
	public ResponseEntity<List<UserDto>> findAllByOrganizationId(@PathVariable String organizationId) {
		List<UserDto> users = (List<UserDto>) userService.findAllByOrganizationId(organizationId);
					
		log.debug("findAllByOrganizationId: found {} users", users.size());
		
		return new ResponseEntity<List<UserDto>>(users, HttpStatus.OK);			
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{email}/email")	
	public ResponseEntity<User> findByEmail(@PathVariable String email) {
		User user = userService.findByEmail(email);
										
		log.debug("findByEmail: found user with email: {}", email);
			
		return new ResponseEntity<User>(user, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{userId}/cases")	
	public ResponseEntity<CaseUserDto> findCasesByUser(@PathVariable String userId) {
		CaseUserDto userCaseDto = userService.findCasesByUser(userId);
										
		log.debug("findByUserAggregate: found user cases with userId: {}", userId);
			
		return new ResponseEntity<CaseUserDto>(userCaseDto, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json")	
	public ResponseEntity<User> save(@RequestBody UserRequestDto userRequestDto) {		
		User userSaved = userService.save(userRequestDto);

		log.debug("save: create/update user with userId: {} from Manager Service", userSaved.getUserId());
			
		return new ResponseEntity<User>(userSaved, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.DELETE },value = "/{userId}")	
	public void deleteById(@PathVariable String userId) throws Exception {
		log.debug("deleteById: remove user with userId: {}", userId);
			
		userService.deleteById(userId);					
	}
			
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json", value = "/{userId}/resetPassword")	
	public void resetPassword(@PathVariable String userId, @RequestBody String password) throws Exception {
		userService.resetPassword(userId, password);
										
		log.debug("resetPassword: reset password user with userId: {}", userId);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json", value = "/inviteUser")	
	public ResponseEntity<User> inviteUser(@RequestBody UserInviteRequestDto userInviteRequestDto) throws Exception {		
		User user = userService.inviteUser(userInviteRequestDto);

		log.debug("save: create/update user with userId: {} from Manager Service", user.getUserId());
			
		return new ResponseEntity<User>(user, HttpStatus.OK);			
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/realms/{realm}/sessions")	
	public ResponseEntity<UserSessionDto[]> getClientUserSessions(@PathVariable String realm) throws Exception {
		UserSessionDto[] userSessionDtos = userService.getClientUserSessions(realm);
			
		return new ResponseEntity<UserSessionDto[]>(userSessionDtos, HttpStatus.OK);		
	}	
}
