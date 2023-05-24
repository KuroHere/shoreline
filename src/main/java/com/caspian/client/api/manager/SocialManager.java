package com.caspian.client.api.manager;

import com.caspian.client.api.social.Relation;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Manages client social relationships by storing the associated player
 * {@link UUID}'s with a {@link Relation} value to the user. Backed by a
 * {@link ConcurrentMap} so getting/setting runs in O(1).
 *
 * @author linus
 * @since 1.0
 *
 * @see UUID
 * @see Relation
 */
public class SocialManager
{
    //
    private final ConcurrentMap<UUID, Relation> relationships =
            new ConcurrentHashMap<>();

    /**
     *
     *
     * @param uuid
     * @param relation
     * @return
     */
    public boolean isRelation(UUID uuid, Relation relation)
    {
        return relationships.get(uuid) == relation;
    }

    /**
     *
     *
     * @param uuid
     * @return
     *
     * @see #isRelation(UUID, Relation)
     */
    public boolean isFriend(UUID uuid)
    {
        return isRelation(uuid, Relation.FRIEND);
    }

    /**
     *
     *
     * @param uuid
     * @param relation
     */
    public void addRelation(UUID uuid, Relation relation)
    {
        Relation relationship = relationships.get(uuid);
        if (relationship != null)
        {
            relationships.replace(uuid, relation);
            return;
        }

        relationships.put(uuid, relation);
    }

    /**
     *
     *
     * @param uuid
     *
     * @see #addRelation(UUID, Relation)
     */
    public void addFriend(UUID uuid)
    {
        addRelation(uuid, Relation.FRIEND);
    }

    /**
     *
     *
     * @param relation
     * @return
     */
    public Collection<UUID> getRelations(Relation relation)
    {
        List<UUID> friends = new ArrayList<>();
        for (Map.Entry<UUID, Relation> relationship : relationships.entrySet())
        {
            if (relationship.getValue() == relation)
            {
                friends.add(relationship.getKey());
            }
        }

        return friends;
    }

    /**
     *
     *
     * @return
     *
     * @see #getRelations(Relation)
     */
    public Collection<UUID> getFriends()
    {
        return getRelations(Relation.FRIEND);
    }
}
