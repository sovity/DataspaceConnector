package de.fraunhofer.isst.dataspaceconnector.model.view;

import de.fraunhofer.isst.dataspaceconnector.controller.resources.RelationshipControllers;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RepresentationArtifactController;
import de.fraunhofer.isst.dataspaceconnector.controller.resources.RepresentationController;
import de.fraunhofer.isst.dataspaceconnector.exceptions.UnreachableLineException;
import de.fraunhofer.isst.dataspaceconnector.model.OfferedResource;
import de.fraunhofer.isst.dataspaceconnector.model.Representation;
import de.fraunhofer.isst.dataspaceconnector.model.RequestedResource;
import de.fraunhofer.isst.dataspaceconnector.utils.ErrorMessages;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;

/**
 * Assembles the REST resource for an representation.
 */
@Component
@NoArgsConstructor
public class RepresentationViewAssembler
        implements RepresentationModelAssembler<Representation, RepresentationView> {
    /**
     * Construct the RepresentationView from an Representation.
     * @param representation The representation.
     * @return The new view.
     */
    @Override
    public RepresentationView toModel(final Representation representation) {
        final var modelMapper = new ModelMapper();
        final var view = modelMapper.map(representation, RepresentationView.class);

        final var selfLink =
                linkTo(RepresentationController.class).slash(representation.getId()).withSelfRel();
        view.add(selfLink);

        final var artifactsLink =
                linkTo(methodOn(RepresentationArtifactController.class)
                                .getResource(representation.getId(), null, null, null))
                        .withRel("artifacts");
        view.add(artifactsLink);

        final var resourceType = representation.getResources();
        Link resourceLinker;
        if (resourceType.isEmpty()) {
            // No elements found, default to offered resources
            resourceLinker =
                    linkTo(methodOn(RelationshipControllers.ContractsToOfferedResources.class)
                                    .getResource(representation.getId(), null, null, null))
                            .withRel("offered");
        } else {
            // Construct the link for the right resource type.
            if (resourceType.get(0) instanceof OfferedResource) {
                resourceLinker = linkTo(
                        methodOn(RelationshipControllers.RepresentationsToOfferedResources.class)
                                .getResource(representation.getId(), null, null, null))
                                         .withRel("offered");
            } else if (resourceType.get(0) instanceof RequestedResource) {
                resourceLinker = linkTo(
                        methodOn(RelationshipControllers.RepresentationsToRequestedResources.class)
                                .getResource(representation.getId(), null, null, null))
                                         .withRel("requested");
            } else {
                throw new UnreachableLineException(ErrorMessages.UNKNOWN_TYPE);
            }
        }

        view.add(resourceLinker);

        return view;
    }
}
