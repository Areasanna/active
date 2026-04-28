package com.example.active.exercicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ExerciseService {
    @Autowired
    private ExerciseRepository exerciseRepository;

   // @Autowired
  //  private MusculoRespository musculoRespository;

//  @Autowired
 // private EquipamentoRepository equipamentoRepository;

    @Transactional
    public ExerciseResponse create(ExerciseRequest request){
        Exercicio exercicio = new Exercicio();
        exercicio.setTitle(request.title());
        exercicio.setCategory(request.category());
        exercicio.setDescription(request.description());

        //buscar as referencia no banco pelos IDs enviados
      //  exercicio.setMusculoPrimario(musculoRepository.findAllById(request.musculoPrimario()));
        //exercicio.setEquipamentos(equipamentosRepository.findAllById(request.equipamentosIds()))

        exercicio = exerciseRepository.save(exercicio);
        return mapToResponse(exercicio);
    }
    private ExerciseResponse mapToResponse(Exercicio exercicio){
        return new ExerciseResponse(
                exercicio.getId(),
                exercicio.getTitle(),
                exercicio.getCategory(),
                exercicio.getMusculoPrimario().stream().map()

        );
    }


}
