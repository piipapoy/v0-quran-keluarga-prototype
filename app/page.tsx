'use client'

import { AppProvider, useApp } from '@/lib/app-context'
import { WelcomeScreen } from '@/components/screens/welcome-screen'
import { HomeScreen } from '@/components/screens/home-screen'
import { ReaderScreen } from '@/components/screens/reader-screen'
import { FamilyScreen } from '@/components/screens/family-screen'
import { SettingsScreen } from '@/components/screens/settings-screen'

function AppContent() {
  const { state } = useApp()

  // Show welcome screen if not onboarded
  if (!state.isOnboarded) {
    return <WelcomeScreen />
  }

  // Render current screen based on state
  switch (state.screen) {
    case 'home':
      return <HomeScreen />
    case 'reader':
      return <ReaderScreen />
    case 'family':
      return <FamilyScreen />
    case 'settings':
      return <SettingsScreen />
    default:
      return <HomeScreen />
  }
}

export default function QuranKeluargaApp() {
  return (
    <AppProvider>
      <div className="max-w-md mx-auto min-h-screen bg-background relative">
        {/* Mobile frame simulation for desktop viewing */}
        <div className="fixed inset-0 bg-muted/30 -z-10 hidden md:block" />
        <div className="md:shadow-2xl md:rounded-3xl md:overflow-hidden md:my-8 md:mx-auto md:max-w-[390px] min-h-screen md:min-h-[calc(100vh-64px)] md:border md:border-border/50">
          <AppContent />
        </div>
      </div>
    </AppProvider>
  )
}
