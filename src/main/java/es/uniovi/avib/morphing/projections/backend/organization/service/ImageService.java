package es.uniovi.avib.morphing.projections.backend.organization.service;

import java.util.List;
import java.util.Optional;

import org.bson.types.ObjectId;
import org.springframework.stereotype.Service;

import es.uniovi.avib.morphing.projections.backend.organization.repository.ImageRepository;
import es.uniovi.avib.morphing.projections.backend.organization.domain.Image;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ImageService {
	private final ImageRepository imageRepository;
	
	public List<Image> findAll() {		
		log.debug("findAll: found all images");
		
		return (List<Image>) imageRepository.findAll();		
	}
	
	public Image findById(String imageId) {
		log.debug("findById: found image with id: {}", imageId);
		 
		return imageRepository.findById(imageId).orElseThrow(() -> new RuntimeException("Image not found"));	
	}
	
	public List<Image> findByOrganizationId(String organizationId) {
		log.debug("findByOrganizationId: found image with organizationId: {}", organizationId);
		
		return imageRepository.findByOrganizationId(new ObjectId(organizationId));	
	}
	
	public Image save(Image image) {
		log.debug("save: save image");
		
		return imageRepository.save(image);
	}
	
	public void deleteById(String imageId) {
		log.debug("deleteById: delete Image with id: {}", imageId);
		
		// get all cases from project
		Optional<Image> image = imageRepository.findById(imageId);
				
		// delete all cases from project
		if (!image.isEmpty())
			imageRepository.delete(image.get());
	}	
}
