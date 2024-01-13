import type { PeerConnection } from "$lib/PeerConnection";
import { writable, type Writable } from "svelte/store"

export const session_uuid: Writable<string> = writable("");
export let peers: Writable<PeerConnection[]> = writable([]);