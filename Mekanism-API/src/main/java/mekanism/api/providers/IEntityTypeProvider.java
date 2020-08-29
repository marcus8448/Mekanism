package mekanism.api.providers;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityType;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public interface IEntityTypeProvider extends IBaseProvider {

    @Nonnull
    EntityType<?> getEntityType();

    @Override
    default Identifier getRegistryName() {
        return getEntityType().getRegistryName();
    }

    @Override
    default Text getTextComponent() {
        return getEntityType().getName();
    }

    @Override
    default String getTranslationKey() {
        return getEntityType().getTranslationKey();
    }
}