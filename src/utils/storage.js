const KEYS = {
  user: 'photo_booking_user',
  likes: 'photo_booking_likes',
  favorites: 'photo_booking_favorites',
  orders: 'photo_booking_orders',
  reviews: 'photo_booking_reviews',
  identifies: 'photo_booking_identifies'
}

export function getUser() {
  const raw = localStorage.getItem(KEYS.user)
  return raw ? JSON.parse(raw) : null
}

export function setUser(user) {
  if (user) localStorage.setItem(KEYS.user, JSON.stringify(user))
  else localStorage.removeItem(KEYS.user)
}

export function getList(key, defaultVal = []) {
  const raw = localStorage.getItem(KEYS[key])
  return raw ? JSON.parse(raw) : [...defaultVal]
}

export function setList(key, list) {
  localStorage.setItem(KEYS[key], JSON.stringify(list))
}

export { KEYS }
