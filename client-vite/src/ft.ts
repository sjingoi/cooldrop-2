import FileTransfer from './FileTransfer.svelte'

const app = new FileTransfer({
  target: document.getElementById('app')!,
})

export default app;