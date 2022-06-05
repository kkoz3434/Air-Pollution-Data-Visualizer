package pl.edu.agh.apdvbackend.use_cases.field;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import pl.edu.agh.apdvbackend.controllers.field.body_models.AddFieldBodyRequest;
import pl.edu.agh.apdvbackend.mappers.FieldMapper;
import pl.edu.agh.apdvbackend.models.Field;
import pl.edu.agh.apdvbackend.repositories.FieldRepository;

@Component
@RequiredArgsConstructor
public class SaveNewFieldImpl
        implements SaveNewField {

    private final FieldRepository fieldRepository;

    private final FieldMapper fieldMapper;

    @Override
    public Field execute(AddFieldBodyRequest addFieldBodyRequest) {
        //final var field = fieldMapper.AddRequestBodyToField(addFieldBodyRequest);
        final var field = new Field();
        field.setLabel(addFieldBodyRequest.label());
        return fieldRepository.save(field);
    }
}
