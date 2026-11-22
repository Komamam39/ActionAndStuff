package com.ans.client.molang;

import org.jspecify.annotations.Nullable;

public record AttachableContext(String itemSlot, @Nullable MolangActorRef owner, String ownerIdentifier, boolean firstPerson, boolean parentSetup) {
    public @Nullable Object value(String name) {
        return switch (name) {
            case "item_slot" -> this.itemSlot;
            case "owning_entity" -> this.owner;
            case "is_first_person" -> this.firstPerson ? 1.0 : 0.0;
            default -> null;
        };
    }
}
