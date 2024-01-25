import FileTransfer from './FileTransfer.svelte'
import SebSite from './SebSite.svelte'

const app = new FileTransfer({
  target: document.getElementById('app')!,
})

export default app;