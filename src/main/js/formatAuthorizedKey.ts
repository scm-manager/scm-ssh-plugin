export function formatAuthorizedKey(key: string) {
  const parts = key.split(/\s+/);
  if (parts.length === 3) {
    return parts[0] + " ... " + parts[2];
  } else if (parts.length === 2) {
    if (parts[0].length >= parts[1].length) {
      return parts[0].substring(0, 7) + "... " + parts[1];
    } else {
      const keyLength = parts[1].length;
      return parts[0] + " ..." + parts[1].substring(keyLength - 7);
    }
  } else {
    const keyLength = parts[0].length;
    if (keyLength < 15) {
      return parts[0];
    }
    return parts[0].substring(0, 7) + "..." + parts[0].substring(keyLength - 7);
  }
}
