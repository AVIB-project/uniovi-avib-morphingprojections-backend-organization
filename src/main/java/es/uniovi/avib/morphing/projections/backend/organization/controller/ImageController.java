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

import es.uniovi.avib.morphing.projections.backend.organization.domain.Image;
import es.uniovi.avib.morphing.projections.backend.organization.service.ImageService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@CrossOrigin(maxAge = 3600)
@RestController
@RequiredArgsConstructor
@RequestMapping("images")
public class ImageController {
	private final ImageService imageService;
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json")
	public ResponseEntity<List<Image>> findAll() {
		List<Image> resources = (List<Image>) imageService.findAll();
					
		log.debug("findAll: found {} images", resources.size());
		
		return new ResponseEntity<List<Image>>(resources, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/{imageId}")	
	public ResponseEntity<Image> findById(@PathVariable String imageId) {
		Image image = imageService.findById(imageId);
										
		log.debug("findById: found image with imageId: {}", imageId);
			
		return new ResponseEntity<Image>(image, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.GET }, produces = "application/json", value = "/organizations/{organizationId}")	
	public ResponseEntity<List<Image>> findByOrganizationId(@PathVariable String organizationId) {
		List<Image> images = imageService.findByOrganizationId(organizationId);
										
		log.debug("findById: find resources with caseId: {}", organizationId);
			
		return new ResponseEntity<List<Image>>(images, HttpStatus.OK);		
	}
	
	@RequestMapping(method = { RequestMethod.POST }, produces = "application/json")	
	public ResponseEntity<Image> save(@RequestBody Image image) {		
		Image imageSaved = imageService.save(image);

		log.debug("save: create/update image with image id: {} from Manager Service", imageSaved.getImageId());
			
		return new ResponseEntity<Image>(imageSaved, HttpStatus.OK);			
	}

	@RequestMapping(method = { RequestMethod.DELETE },value = "/{imageId}")	
	public void deleteById(@PathVariable String imageId) {
		log.debug("deleteById: remove image with imageId: {}", imageId);
			
		imageService.deleteById(imageId);					
	}  
}
