package io.github.projectet.ae2things.compat.emi;

import dev.emi.emi.api.EmiEntrypoint;
import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.stack.EmiStack;
import io.github.projectet.ae2things.definitions.AE2TBlocks;
import io.github.projectet.ae2things.definitions.AE2TRecipeTypes;

@EmiEntrypoint
public class EMI implements EmiPlugin {
    @Override
    public void register(EmiRegistry registry) {
        //ae2t inscriber now is displayed near ae2 inscriber
        try {
            Class<?> clazz = Class.forName("appeng.integration.modules.emi.EmiInscriberRecipe");
            java.lang.reflect.Field field = clazz.getDeclaredField("CATEGORY");
            field.setAccessible(true);

            EmiRecipeCategory category = (EmiRecipeCategory) field.get(null);
            registry.addWorkstation(category, EmiStack.of(AE2TBlocks.ADVANCED_INSCRIBER));
        } catch (Exception e) {
            e.printStackTrace();
        }
        //crystal growth
        registry.addCategory(EmiCrystalGrowthRecipe.CATEGORY);
        registry.getRecipeManager().getAllRecipesFor(AE2TRecipeTypes.CRYSTAL_GROWTH_RECIPE)
                .stream()
                .map(EmiCrystalGrowthRecipe::new)
                .forEach(registry::addRecipe);
    }
}
