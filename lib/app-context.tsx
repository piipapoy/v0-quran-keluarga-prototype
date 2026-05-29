'use client'

import { createContext, useContext, useState, ReactNode } from 'react'

export type Screen = 'welcome' | 'home' | 'reader' | 'family' | 'settings'
export type Theme = 'light' | 'sepia' | 'dark'

export interface FamilyMember {
  id: string
  name: string
  avatar: string
  lastSeen: string
  lastUpdate?: string
}

export interface Activity {
  id: string
  member: string
  action: string
  surah: string
  ayat: number
  time: string
}

export interface AppState {
  screen: Screen
  theme: Theme
  isOnboarded: boolean
  familyName: string
  familyCode: string
  currentSurah: string
  currentAyat: number
  lastUpdatedBy: string
  lastUpdatedTime: string
  personalBookmark: { surah: string; ayat: number } | null
  targetKhatam: string
  fontSize: number
  showTranslation: boolean
  members: FamilyMember[]
  activities: Activity[]
}

interface AppContextType {
  state: AppState
  setScreen: (screen: Screen) => void
  setTheme: (theme: Theme) => void
  setOnboarded: (value: boolean) => void
  updateFamilyProgress: (surah: string, ayat: number) => void
  setPersonalBookmark: (surah: string, ayat: number) => void
  setFontSize: (size: number) => void
  toggleTranslation: () => void
}

const initialState: AppState = {
  screen: 'welcome',
  theme: 'light',
  isOnboarded: false,
  familyName: 'Keluarga Hasan',
  familyCode: 'HSN-204',
  currentSurah: 'Al-Baqarah',
  currentAyat: 45,
  lastUpdatedBy: 'Ibu',
  lastUpdatedTime: '05:42',
  personalBookmark: { surah: 'An-Nisa', ayat: 12 },
  targetKhatam: 'Ramadan 1446',
  fontSize: 28,
  showTranslation: true,
  members: [
    { id: '1', name: 'Ayah', avatar: 'A', lastSeen: 'Online', lastUpdate: 'Hari ini, 05:42' },
    { id: '2', name: 'Ibu', avatar: 'I', lastSeen: 'Online', lastUpdate: 'Hari ini, 05:42' },
    { id: '3', name: 'Aisyah', avatar: 'A', lastSeen: '2 jam lalu', lastUpdate: 'Kemarin, 19:30' },
    { id: '4', name: 'Ahmad', avatar: 'A', lastSeen: '5 jam lalu', lastUpdate: '2 hari lalu' },
  ],
  activities: [
    { id: '1', member: 'Ibu', action: 'memperbarui progress ke', surah: 'Al-Baqarah', ayat: 45, time: '05:42' },
    { id: '2', member: 'Ayah', action: 'memindahkan progress ke', surah: 'Al-Baqarah', ayat: 40, time: 'Kemarin' },
    { id: '3', member: 'Aisyah', action: 'menandai', surah: 'An-Nisa', ayat: 12, time: 'Kemarin' },
  ],
}

const AppContext = createContext<AppContextType | null>(null)

export function AppProvider({ children }: { children: ReactNode }) {
  const [state, setState] = useState<AppState>(initialState)

  const setScreen = (screen: Screen) => {
    setState(prev => ({ ...prev, screen }))
  }

  const setTheme = (theme: Theme) => {
    setState(prev => ({ ...prev, theme }))
    if (typeof document !== 'undefined') {
      document.documentElement.classList.remove('light', 'dark', 'sepia')
      if (theme !== 'light') {
        document.documentElement.classList.add(theme)
      }
    }
  }

  const setOnboarded = (value: boolean) => {
    setState(prev => ({ ...prev, isOnboarded: value, screen: value ? 'home' : 'welcome' }))
  }

  const updateFamilyProgress = (surah: string, ayat: number) => {
    const now = new Date()
    const timeStr = now.toLocaleTimeString('id-ID', { hour: '2-digit', minute: '2-digit' })
    setState(prev => ({
      ...prev,
      currentSurah: surah,
      currentAyat: ayat,
      lastUpdatedBy: 'Anda',
      lastUpdatedTime: timeStr,
      activities: [
        {
          id: Date.now().toString(),
          member: 'Anda',
          action: 'memperbarui progress ke',
          surah,
          ayat,
          time: timeStr,
        },
        ...prev.activities,
      ],
    }))
  }

  const setPersonalBookmark = (surah: string, ayat: number) => {
    setState(prev => ({
      ...prev,
      personalBookmark: { surah, ayat },
    }))
  }

  const setFontSize = (size: number) => {
    setState(prev => ({ ...prev, fontSize: size }))
  }

  const toggleTranslation = () => {
    setState(prev => ({ ...prev, showTranslation: !prev.showTranslation }))
  }

  return (
    <AppContext.Provider
      value={{
        state,
        setScreen,
        setTheme,
        setOnboarded,
        updateFamilyProgress,
        setPersonalBookmark,
        setFontSize,
        toggleTranslation,
      }}
    >
      {children}
    </AppContext.Provider>
  )
}

export function useApp() {
  const context = useContext(AppContext)
  if (!context) {
    throw new Error('useApp must be used within AppProvider')
  }
  return context
}
