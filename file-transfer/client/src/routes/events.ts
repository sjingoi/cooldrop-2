
interface SDPEvent extends Event {
    detail: {
        sdp: string;
    }
}

// interface SDPEventInit<T = any> extends EventInit {
//     sdp: string,
// }

// declare var SDPEvent: {
//     prototype: SDPEvent;
//     new(type: string, eventInitDict?: SDPEventInit): SDPEvent;
// };
