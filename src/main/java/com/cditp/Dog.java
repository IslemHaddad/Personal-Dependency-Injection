package com.cditp;


@Component
public class Dog {

    @Inject
    @Qualifier("Eat")
    private Actions actions;

   public void doAction(){
      actions.doAction();
   }

   public Actions getActions() {
      return this.actions;
   }

   public void setActions(Actions actions) {
      this.actions = actions;
   }

}
