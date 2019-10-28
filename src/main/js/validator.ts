const portPattern = /^[0-9]+$/;
const minPort = 1;
const maxPort = 65535;

export function validatePort(value?: string) {
  if (value && portPattern.test(value)) {
    const port = parseInt(value);
    return port >= minPort && port <= maxPort;
  }
  return false;
}

const sshHostnameWithPortPattern = /^(ssh:\/\/)?[^: \/]+(:[0-9]+)?$/;

export function validateHostnameWithPort(value?: string) {
  if (!value) {
    return true;
  }
  return sshHostnameWithPortPattern.test(value);
}
