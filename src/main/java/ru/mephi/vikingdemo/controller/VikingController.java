package ru.mephi.vikingdemo.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.mephi.vikingdemo.model.BeardStyle;
import ru.mephi.vikingdemo.model.HairColor;
import ru.mephi.vikingdemo.model.Viking;
import ru.mephi.vikingdemo.service.LambdaVikingService;
import ru.mephi.vikingdemo.service.VikingService;

import java.util.List;

@RestController
@RequestMapping("/api/vikings")
@Tag(name = "Vikings", description = "Операции с викингами")
public class VikingController {

    private final VikingService vikingService;
    private final VikingListener vikingListener;
    private final LambdaVikingService lambdaVikingService;

    public VikingController(VikingService vikingService,
                            VikingListener vikingListener,
                            LambdaVikingService lambdaVikingService) {
        this.vikingService = vikingService;
        this.vikingListener = vikingListener;
        this.lambdaVikingService = lambdaVikingService;
    }

    @GetMapping
    @Operation(summary = "Получить список созданных викингов", operationId = "getAllVikings")
    @ApiResponses({@ApiResponse(responseCode = "200", description = "Список успешно получен")})
    public List<Viking> getAllVikings() {
        return vikingService.findAll();
    }

    @GetMapping("/test")
    public List<String> test() {
        return List.of("Ragnar", "Bjorn");
    }

    @PostMapping("/random")
    public ResponseEntity<Void> addRandomViking() {
        vikingListener.addRandomViking();
        return ResponseEntity.ok().build();
    }

    @PostMapping("/generate/{count}")
    public ResponseEntity<Void> generateVikings(@PathVariable int count) {
        vikingListener.addRandomVikings(count);
        return ResponseEntity.ok().build();
    }

    @PostMapping
    public ResponseEntity<Viking> addViking(@RequestBody Viking viking) {
        vikingListener.addViking(viking);
        return ResponseEntity.ok(viking);
    }

    @DeleteMapping("/{name}")
    public ResponseEntity<Void> deleteViking(@PathVariable String name) {
        return vikingListener.removeViking(name)
                ? ResponseEntity.noContent().build()
                : ResponseEntity.notFound().build();
    }

    @PutMapping("/{name}")
    public ResponseEntity<Viking> updateViking(@PathVariable String name, @RequestBody Viking viking) {
        return vikingListener.updateViking(name, viking)
                ? ResponseEntity.ok(viking)
                : ResponseEntity.notFound().build();
    }

    @GetMapping("/lambda/count/age/greater-than/{age}")
    public long countAgeGreaterThan(@PathVariable int age) {
        return lambdaVikingService.countAgeGreaterThan(age);
    }

    @GetMapping("/lambda/count/age/less-than/{age}")
    public long countAgeLessThan(@PathVariable int age) {
        return lambdaVikingService.countAgeLessThan(age);
    }

    @GetMapping("/lambda/count/age/in-range/{min}/{max}")
    public long countAgeInRange(@PathVariable int min, @PathVariable int max) {
        return lambdaVikingService.countAgeInRange(min, max);
    }

    @GetMapping("/lambda/count/age/outside-range/{min}/{max}")
    public long countAgeOutsideRange(@PathVariable int min, @PathVariable int max) {
        return lambdaVikingService.countAgeOutsideRange(min, max);
    }

    @GetMapping("/lambda/count/beard-hair/{beardStyle}/{hairColor}")
    public long countByBeardAndHair(@PathVariable BeardStyle beardStyle, @PathVariable HairColor hairColor) {
        return lambdaVikingService.countByBeardStyleAndHairColor(beardStyle, hairColor);
    }

    @GetMapping("/lambda/count/one-axe")
    public long countWithOneAxe() {
        return lambdaVikingService.countWithOneAxe();
    }

    @GetMapping("/lambda/count/two-axes")
    public long countWithTwoAxes() {
        return lambdaVikingService.countWithTwoAxes();
    }

    @GetMapping("/lambda/random/taller-than-180")
    public ResponseEntity<Viking> randomTallerThan180() {
        return lambdaVikingService.findRandomVikingTallerThan180()
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/lambda/legendary")
    public List<Viking> legendaryEquipment() {
        return lambdaVikingService.findAllWithLegendaryEquipment();
    }

    @GetMapping("/lambda/red-bearded/sorted-by-age")
    public List<Viking> redBeardedSortedByAge() {
        return lambdaVikingService.findRedBeardedSortedByAge();
    }

    @PostMapping("/lambda/ids/max")
    public ResponseEntity<Integer> maxId(@RequestBody Integer[] ids) {
        return lambdaVikingService.findMaxId(ids)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @PostMapping("/lambda/ids/even")
    public List<Integer> evenIds(@RequestBody Integer[] ids) {
        return lambdaVikingService.findEvenIds(ids);
    }
}
