'use client'

import { Moon, Sun, Type, Languages, Shield, Heart, LogOut, ChevronRight } from 'lucide-react'
import { useApp, Theme } from '@/lib/app-context'
import { Card } from '@/components/ui/card'
import { Switch } from '@/components/ui/switch'
import { Slider } from '@/components/ui/slider'
import { BottomNav } from '@/components/bottom-nav'

export function SettingsScreen() {
  const { state, setTheme, setFontSize, toggleTranslation } = useApp()

  return (
    <div className="min-h-screen bg-background pb-24">
      {/* Header */}
      <header className="px-5 pt-12 pb-6">
        <h1 className="text-2xl font-semibold text-foreground">Pengaturan</h1>
      </header>

      <main className="px-5 space-y-6">
        {/* Reading Settings */}
        <section>
          <h3 className="text-sm font-medium text-muted-foreground mb-3 px-1">Tampilan Bacaan</h3>
          <Card className="rounded-2xl border border-border/50 divide-y divide-border/50 overflow-hidden">
            {/* Font Size */}
            <div className="p-4">
              <div className="flex items-center gap-3 mb-4">
                <div className="w-10 h-10 rounded-xl bg-secondary flex items-center justify-center">
                  <Type className="w-5 h-5 text-foreground" />
                </div>
                <div className="flex-1">
                  <p className="font-medium text-foreground">Ukuran Font Arab</p>
                  <p className="text-xs text-muted-foreground">{state.fontSize}px</p>
                </div>
              </div>
              <div className="flex items-center gap-4">
                <span className="text-sm text-muted-foreground">A</span>
                <Slider
                  value={[state.fontSize]}
                  onValueChange={([value]) => setFontSize(value)}
                  min={20}
                  max={40}
                  step={2}
                  className="flex-1"
                />
                <span className="text-lg text-muted-foreground">A</span>
              </div>
            </div>

            {/* Translation Toggle */}
            <div className="flex items-center justify-between p-4">
              <div className="flex items-center gap-3">
                <div className="w-10 h-10 rounded-xl bg-secondary flex items-center justify-center">
                  <Languages className="w-5 h-5 text-foreground" />
                </div>
                <div>
                  <p className="font-medium text-foreground">Tampilkan Terjemahan</p>
                  <p className="text-xs text-muted-foreground">Bahasa Indonesia</p>
                </div>
              </div>
              <Switch
                checked={state.showTranslation}
                onCheckedChange={toggleTranslation}
              />
            </div>
          </Card>
        </section>

        {/* Theme Settings */}
        <section>
          <h3 className="text-sm font-medium text-muted-foreground mb-3 px-1">Tema</h3>
          <Card className="rounded-2xl border border-border/50 p-4">
            <div className="grid grid-cols-3 gap-3">
              <ThemeButton
                theme="light"
                label="Terang"
                icon={<Sun className="w-5 h-5" />}
                isActive={state.theme === 'light'}
                onClick={() => setTheme('light')}
              />
              <ThemeButton
                theme="sepia"
                label="Sepia"
                icon={<Moon className="w-5 h-5" />}
                isActive={state.theme === 'sepia'}
                onClick={() => setTheme('sepia')}
              />
              <ThemeButton
                theme="dark"
                label="Gelap"
                icon={<Moon className="w-5 h-5" />}
                isActive={state.theme === 'dark'}
                onClick={() => setTheme('dark')}
              />
            </div>
          </Card>
        </section>

        {/* Account */}
        <section>
          <h3 className="text-sm font-medium text-muted-foreground mb-3 px-1">Akun</h3>
          <Card className="rounded-2xl border border-border/50 divide-y divide-border/50 overflow-hidden">
            <SettingsItem
              icon={<Shield className="w-5 h-5 text-foreground" />}
              label="Privasi & Keamanan"
            />
            <SettingsItem
              icon={<Heart className="w-5 h-5 text-foreground" />}
              label="Tentang Aplikasi"
            />
            <SettingsItem
              icon={<LogOut className="w-5 h-5 text-destructive" />}
              label="Keluar"
              destructive
            />
          </Card>
        </section>

        {/* Ad-Free Statement */}
        <Card className="p-4 rounded-2xl bg-primary/5 border-0">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-full bg-primary/10 flex items-center justify-center">
              <Heart className="w-5 h-5 text-primary" />
            </div>
            <div>
              <p className="font-medium text-foreground text-sm">Bebas Iklan</p>
              <p className="text-xs text-muted-foreground">
                Aplikasi ini sepenuhnya bebas iklan untuk pengalaman ibadah yang fokus.
              </p>
            </div>
          </div>
        </Card>

        {/* Version */}
        <p className="text-center text-xs text-muted-foreground">
          Quran Keluarga v1.0.0
        </p>
      </main>

      <BottomNav />
    </div>
  )
}

function ThemeButton({
  theme,
  label,
  icon,
  isActive,
  onClick,
}: {
  theme: Theme
  label: string
  icon: React.ReactNode
  isActive: boolean
  onClick: () => void
}) {
  const bgColors: Record<Theme, string> = {
    light: 'bg-[#f5f3ef]',
    sepia: 'bg-[#e8dcc8]',
    dark: 'bg-[#1a1a1a]',
  }

  const textColors: Record<Theme, string> = {
    light: 'text-gray-800',
    sepia: 'text-amber-900',
    dark: 'text-white',
  }

  return (
    <button
      onClick={onClick}
      className={`flex flex-col items-center gap-2 p-3 rounded-xl border-2 transition-all ${
        isActive
          ? 'border-primary bg-primary/5'
          : 'border-transparent hover:bg-secondary'
      }`}
    >
      <div className={`w-12 h-12 rounded-xl ${bgColors[theme]} flex items-center justify-center ${textColors[theme]}`}>
        {icon}
      </div>
      <span className={`text-xs font-medium ${isActive ? 'text-primary' : 'text-foreground'}`}>
        {label}
      </span>
    </button>
  )
}

function SettingsItem({
  icon,
  label,
  destructive,
}: {
  icon: React.ReactNode
  label: string
  destructive?: boolean
}) {
  return (
    <button className="flex items-center gap-3 p-4 w-full hover:bg-secondary/50 transition-colors">
      <div className="w-10 h-10 rounded-xl bg-secondary flex items-center justify-center">
        {icon}
      </div>
      <span className={`flex-1 text-left font-medium ${destructive ? 'text-destructive' : 'text-foreground'}`}>
        {label}
      </span>
      <ChevronRight className="w-5 h-5 text-muted-foreground" />
    </button>
  )
}
