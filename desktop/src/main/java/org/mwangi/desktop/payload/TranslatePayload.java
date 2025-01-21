package org.mwangi.desktop.payload;

/**
 * represents  the relationship of what our payload could be
 * <p>
 *     This interface is just  a way to model alternatives
 * </p>
 */
public sealed interface TranslatePayload permits AndroidPayload,IOSPayload {

}
