package classes.entities.animals;

import classes.entities.Direction;
import classes.terrains.Terrain;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Herd {
    private ArrayList<Animal> members = new ArrayList<>();
    private Animal leader;

    private static final Random rand = new Random();
    private final int MAX_SIZE = 5;

    public Herd(ArrayList<Animal> animals){
        members = animals;
        setThirstAndHungerLevels();
        setProperties();

        leader = animals.get(rand.nextInt(animals.size()));
    }


    public void setProperties(){
        for(Animal animal : members){
            animal.setIsInAHerd(true);
            animal.setHerd(this);
        }
    }

    public void removeMember(Animal animal) {
        members.remove(animal);

        animal.setIsInAHerd(false);
        animal.setHerd(null);
        animal.transitionTo(AnimalState.IDLE);

        if (animal == leader) {
            if (!members.isEmpty()) {
                leader = members.get(0);
            } else {
                leader = null;
            }
        }


        if (members.size() < 2) {
            for (Animal leftover : members) {
                leftover.setIsInAHerd(false);
                leftover.setHerd(null);
                leftover.transitionTo(AnimalState.IDLE);

                leftover.setStateIconVisibility(false);
                leftover.setRestingTimePassed(0.0);
            }
            members.clear();
            leader = null;
        }
    }



    public void setLeader(Animal newLeader) {
        this.leader = newLeader;
    }


    public void addMember(Animal animal) {
        if (members.size() >= MAX_SIZE) return;

        members.add(animal);
        animal.setIsInAHerd(true);
        animal.setHerd(this);
    }

    public void setThirstAndHungerLevels(){
        double thirst = 0.0;
        double hunger = 0.0;
        for (Animal animal : members){
            thirst += animal.getThirst();
            hunger += animal.getHunger();
        }
        thirst = thirst / members.size();
        hunger = hunger / members.size();

        for(Animal animal : members){
            animal.setThirst(thirst);
            animal.setHunger(hunger);
        }
    }

    public ArrayList<Animal> getMembers(){
        return this.members;
    }
    public int getMemberCount(){
        return this.members.size();
    }
    public Animal getLeader() {
        return this.leader;
    }

    public void assignNewLeader() {
        if (members.size() < 2) return;
        leader = members.get(0);
    }


}
